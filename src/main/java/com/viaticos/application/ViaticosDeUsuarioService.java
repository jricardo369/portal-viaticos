package com.viaticos.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.edicom.ediwinws.service.cfdi.GetCFDiStatusResponse;
import com.itextpdf.text.DocumentException;
import com.viaticos.ApiException;
import com.viaticos.SoapClient;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.adapter.out.file.PdfSolicitudViatico;
import com.viaticos.application.port.in.ViaticosDeUsuarioUseCase;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.NivelesPort;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.ComprobantesDeViaticosPort;
import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.EnviarCorreoPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.SubCuentasContablesPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.Cfdi;
import com.viaticos.domain.CfdiEntity;
import com.viaticos.domain.Comprobante;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.ConfiguracionEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.NivelTopeUsuarioEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.SubCuenta;
import com.viaticos.domain.SubCuentaContableEntity;
import com.viaticos.domain.Usuario;
import com.viaticos.domain.sql.accesos.UsuarioEntity;
import com.viaticos.domain.sql.nu3.RolModel;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
public class ViaticosDeUsuarioService implements ViaticosDeUsuarioUseCase {

	Logger log = LoggerFactory.getLogger(ViaticosDeUsuarioService.class);

	public static String estatusCargaEntrega = "Dispersión entrega";

	@Value("${rutaArchivos}")
	private String rutaArchivos;

	@Value("${soap.url}")
	private String urlSoap;

	@Value("${soap.user}")
	private String userSoap;

	@Value("${soap.pass}")
	private String passSoap;

	@Value("${ambiente}")
	private String ambiente;

	@Autowired
	private SoapClient soapClient;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Autowired
	ComprobantesDeViaticosPort compPort;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EventoDeViaticoPort bitacoraPort;

	@Autowired
	private EnviarCorreoPort envioCorreoPort;

	@Autowired
	private ArchivosPort archivosPort;

	@Autowired
	private CfdiPort cfdiPort;

	@Autowired
	private SubCuentasContablesPort subPort;

	@Autowired
	private NivelesPort nivelesPort;

	@Autowired
	private ConfiguracionesPort configuracionPort;

	@Autowired
	private PdfSolicitudViatico pdfSolicitudViatico;
	

	@Override
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatus(String usuario, String empleado, String estatus) {
		return solUsPort.encontrarSolicitudesDeEmpladoPorEstatus(empleado, estatus);
		// return solUsPort.encontrarSolicitudesDeEmpladoPorEstatusJPA(empleado,
		// estatus);
	}

	@Override
	public void solicitarViaticosParaUsuario(int solicitud, int estatusSolicitud, String usuarioTempus) {

		// Validar si ya esta en dispersion de lo contrario mandar error

		if (solUsPort.obtenerEventoPorSolicitud(estatusCargaEntrega, solicitud) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La solicitud no ha sido dispersada, para poder enviar a aprobación debe contar con su dispersión ");
		}

		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();
		List<ComprobanteViaticoEntity> comprobantes = new ArrayList<>();

		estatusEntity = estatusPort.obtieneEstatusSolicitud(estatusSolicitud);
		solicitudEntity = solUsPort.obtenerSolicitudJPA(solicitud);

		// eventoEntity = this.eventoViatico("CPC", solicitudEntity, "Comprobacion
		// pendiente - Contador", solicitudEntity.getUsuario());

		for (ComprobanteViaticoEntity c : solicitudEntity.getComprobanteViaticosEntity()) {

			c.setEstatusComprobante("PENDIENTE DE APROBACION");
			comprobantes.add(c);

		}

		solUsPort.enviaPeticionAceptacion(solicitud, estatusEntity);
		// solUsPort.crearEventoSolicitud(eventoEntity);
		compPort.guardaEstatusComprobacionesDeSolicitud(comprobantes);
		bitacoraPort.ingresarEventoDeSolicitud("Comprobacion enviada a aprobar", "", solicitudEntity,
				solicitudEntity.getNombreCompletoUsuario().toUpperCase());

	}

	@Override
	public Solicitud obtenerSolicitud(String numeroSolicitud) {
		return solUsPort.obtenerSolicitud(numeroSolicitud);
	}

	@Override
	public void cargaDeComprobante(String numeroSolicitud, Comprobante comprobante, byte[] xml, byte[] pdf) {

		log.info("numero solicitud:" + numeroSolicitud);
		Cfdi cfdi = null;
		boolean conXml = false;

		// Tomar datos de solicitud
		Solicitud s = solUsPort.obtenerSolicitud(numeroSolicitud);
		if (s == null)

			// Valida estrucdtura
			if (xml != null) {
				conXml = true;
				cfdi = cfdiPort.validarCfdi(xml);
			}

		// Validar duplicidad
		if (conXml)
			cfdiPort.existeCfdi(cfdi.getUuid());

		// Formar ruta de archivos;
		String uuid = UUID.randomUUID().toString();
		// String rutaParaArchivos = rutaArchivos
		// + archivosPort.generaRutaArchivo(UtilidadesAdapter.tomarAnioActual(),
		// s.getUsuario(), numeroSolicitud);
		String rutaParaArchivosBD = archivosPort.generaRutaArchivo(UtilidadesAdapter.tomarAnioActual(), s.getUsuario(),
				numeroSolicitud);
		String nombreXml = "/" + uuid + ".xml";
		String nombrePdf = "/" + uuid + ".pdf";

		comprobante.setRutaXml(rutaParaArchivosBD + nombreXml);
		comprobante.setRutaPdf(rutaParaArchivosBD + nombrePdf);

	}

	@Override
	public Comprobante obtenerDeComprobante(int idComprobante) {

		ComprobanteViaticoEntity comprobanteEntity = new ComprobanteViaticoEntity();
		Comprobante comprobante = new Comprobante();

		comprobanteEntity = compPort.obtenerDeComprobante(idComprobante);

		if (comprobanteEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró comprobante, " + idComprobante);

		comprobante = entityToModelComprobante(comprobanteEntity);

		return comprobante;
	}

	@Override
	public void modificarDeComprobante(int idComprobante, Comprobante comprobante, MultipartFile xml,
			MultipartFile pdf) {

	}

	@Override
	public void eliminarDeComprobante(int idComprobante) {

		ComprobanteViaticoEntity c = new ComprobanteViaticoEntity();

		c = compPort.obtenerDeComprobante(idComprobante);

		if (c == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró comprobante, " + idComprobante);

		File filePDF = new File(rutaArchivos + c.getRutaPdf());
		File fileXML = new File(rutaArchivos + c.getRutaXml());

		filePDF.delete();
		fileXML.delete();
		compPort.eliminarDeComprobante(c);

	}

	@Override
	public void solicitarAprobacionDeComprobacion(Solicitud solicitud) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatusJPA(String empleado, String estatusQ) {

		String[] estatusArr = estatusQ.split(",");
		List<Integer> estatus = new ArrayList<Integer>();

		for (String e : estatusArr) {
			estatus.add(Integer.valueOf(e));
		}

		List<SolicitudViaticosEntity> solicitudList = solUsPort.encontrarSolicitudesDeEmpladoPorEstatusJPA(empleado,
				estatus);

		List<Solicitud> solicitud = new ArrayList<Solicitud>();
		Usuario user = new Usuario();

		// Busca en tabla Tempus Nu3
		user = usuariosPort.encontrarUsuarioIdJPA(empleado);

		for (SolicitudViaticosEntity s : solicitudList) {
			solicitud.add(solicitud(s, user));

		}

		return solicitud;
	}

	Comprobante entityToModelComprobante(ComprobanteViaticoEntity c) {
		Comprobante comp = new Comprobante();
		SubCuenta subCuenta = new SubCuenta();

		comp.setIdComprobanteViatico(c.getId());
		comp.setFecha(c.getFechaCarga());

		comp.setImpuesto(c.getImpuestos());
		comp.setSubTotal(c.getSubTotal());
		comp.setTotal(c.getTotal());
		comp.setPropina(c.getPropina());

		subCuenta.setId(c.getSub_cuenta_contable().getId());
		subCuenta.setCodigo(c.getSub_cuenta_contable().getCodigo());
		subCuenta.setDescripcion(c.getSub_cuenta_contable().getDescripcion());
		comp.setSubCuenta(subCuenta);

		comp.setAprobacionContador(c.getAprobacionContador());
		comp.setAprobacionGerente(c.getAprobacionGerente());
		comp.setAprobacionPrestador(c.getAprobacionPrestador());
		comp.setEstatusComprobante(c.getEstatusComprobante());
		comp.setAprobacionNoAplica(c.isAprobacionNoAplica());
		comp.setRutaXml(c.getRutaXml());
		comp.setRutaPdf(c.getRutaPdf());
		comp.setObservaciones(c.getObservaciones());
		comp.setNumeroSolicitud(String.valueOf(c.getNumero_solicitud().getId()));
		comp.setNoAplica(c.getNoAplica());
		comp.setAprobacionNoAplica(c.isAprobacionNoAplica());
		comp.setMontoAprobado(c.getMontoAprobado());

		if (c.getCfdiEntity() != null) {
			comp.setRfc(c.getCfdiEntity().getRfcEmisor());
			comp.setNumeroFactura(c.getCfdiEntity().getNumeroFactura());
			comp.setIva(BigDecimal.valueOf(c.getCfdiEntity().getIva()));
			comp.setIsr(BigDecimal.valueOf(c.getCfdiEntity().getIsr()));
			comp.setIeps(BigDecimal.valueOf(c.getCfdiEntity().getIeps()));
			comp.setCfdi(entityToCfdi(c.getCfdiEntity()));

		}

		return comp;
	}

	Cfdi entityToCfdi(CfdiEntity c) {

		Cfdi cfdi = new Cfdi();

		cfdi.setId(c.getIdCfdi());
		cfdi.setMoneda(c.getMoneda());
		cfdi.setFecha(c.getFecha());
		cfdi.setIvaTrasladado(BigDecimal.valueOf(c.getIva()));
		cfdi.setIsrRetenido(BigDecimal.valueOf(c.getIsr()));
		cfdi.setIshTrasladado(BigDecimal.valueOf(c.getIsh()));
		cfdi.setIepsTrasladado(BigDecimal.valueOf(c.getIeps()));
		// cfdi.settua
		cfdi.setSubtotal(BigDecimal.valueOf(c.getSubtotal()));
		cfdi.setTotal(BigDecimal.valueOf(c.getTotal()));
		cfdi.setRfcEmisor(c.getRfcEmisor());
		cfdi.setRfcReceptor(c.getRfcReceptor());
		cfdi.setUuid(c.getUuid());
		cfdi.setIdComprobante(String.valueOf(c.getId_comprobante_viatico().getId()));
		cfdi.setSerie(c.getSerie());
		cfdi.setFolio(c.getFolio());

		return cfdi;

	}

	public Solicitud solicitud(SolicitudViaticosEntity s, Usuario usuario) {

		Solicitud solicitud = new Solicitud();

		solicitud.setNumeroSolicitud(String.valueOf(s.getId()));
		solicitud.setFechaInicio(s.getFechaInicio());
		solicitud.setFechaFin(s.getFechaFin());
		solicitud.setMotivo(s.getMotivo());
		solicitud.setConcepto(s.getConcepto());
		solicitud.setTotalAnticipo(s.getAnticipo());
		solicitud.setTotalComprobado(new BigDecimal(0));
		solicitud.setEstatus(String.valueOf(s.getEstatus().getId()));
		solicitud.setEstatusDescripcion(s.getEstatus().getDescripcion());
		solicitud.setUsuarioObj(null);
		solicitud.setObservaciones(s.getObservaciones());
		solicitud.setEmpresa(s.getEmpresa());
		solicitud.setCeco(s.getCeco());
		solicitud.setUsuario(s.getUsuario());
		solicitud.setFechaCreacion(s.getFechaCreacion());
		solicitud.setCuentaContable(s.getCuentaContable());
		solicitud.setNivel(s.getNivel());

		if (usuario != null) {
			solicitud.setNombreCompletoUsuario(usuario.getNombre());

			if (usuario.getGrupo01() != null && usuario.getGrupo01().size() > 0) {
				solicitud.setGrupo(usuario.getGrupo01().get(0));
				solicitud.setEmpresaDescr(usuario.getGrupo01().get(0).getNombre());
			}

			if (usuario.getDepartamentos() != null && usuario.getDepartamentos().size() > 0)
				solicitud.setDepartamento(usuario.getDepartamentos().get(0));

			if (usuario.getCecoDesc() != null)
				solicitud.setCecoDesc(usuario.getCecoDesc());

			// if (usuario.get() != null && usuario.getOrganizaciones().size() > 0)
			// solicitud.setEmpresaDescr(usuario.getOrganizaciones().get(0).getNombre());

		}

		List<Comprobante> cList = new ArrayList<>();

		BigDecimal totalComprobado = new BigDecimal("0.00");
		if (s.getComprobanteViaticosEntity() != null) {
			for (ComprobanteViaticoEntity c : s.getComprobanteViaticosEntity()) {
				cList.add(entityToModelComprobante(c));
				totalComprobado = totalComprobado.add(c.getTotal());
			}
		}

		solicitud.setComprobantes(cList);
		solicitud.setTotalComprobado(totalComprobado);

		return solicitud;
	}

	@Override
	public Solicitud obtenerSolicitudJPA(int numeroSolicitud) {

		SolicitudViaticosEntity solicitudEn = new SolicitudViaticosEntity();
		Solicitud solicitud = new Solicitud();
		Usuario user = new Usuario();

		solicitudEn = solUsPort.obtenerSolicitudJPA(numeroSolicitud);

		if (solicitudEn == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		// Busca en tabla Tempus Nu3
		user = usuariosPort.encontrarUsuarioIdJPA(solicitudEn.getUsuario());

		solicitud = this.solicitud(solicitudEn, user);

		return solicitud; // solicitudEn;
	}

	@Override
	public Solicitud crearSolicitud(String empleado, Solicitud solicitud) {

		// Solicitud a SolicitudEntity
		SolicitudViaticosEntity solEnt = new SolicitudViaticosEntity();
		Usuario empleadoInfo = new Usuario();
		EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();

		empleadoInfo = usuariosPort.encontrarUsuarioIdJPA(empleado);

		solicitud.setCeco(empleadoInfo.getCeco());
		solicitud.setNivel(empleadoInfo.getNivel());
		solicitud.setCecoDesc(empleadoInfo.getCecoDesc());

		estatus.setId(1);
		solicitud.setDepartamento(empleadoInfo.getDepartamentos().get(0));
		solicitud.setGrupo(empleadoInfo.getGrupo01().get(0));
		solicitud.setEmpresa(empleadoInfo.getOrganizaciones().get(0).getId());
		solicitud.setEmpresaDescr(empleadoInfo.getOrganizaciones().get(0).getNombre());
		solicitud.setUsuario(empleadoInfo.getUsuario());
		solicitud.setNombreCompletoUsuario(empleadoInfo.getNombre());
		solicitud.setCuentaContable(empleadoInfo.getCuentaContable());

		System.out.println("Rfc:" + empleadoInfo.getRfc());
		solicitud.setRfc(empleadoInfo.getRfc().trim());
		solicitud.setProyecto(empleadoInfo.getProyecto());

		solEnt = solicitudAEntity(solicitud, estatus);

		// Valida fechas
		if (solEnt.getFechaInicio().after(solEnt.getFechaFin()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha inicio mayor que fecha fin");

		// Valida Motivo no vacio
		if (solEnt.getMotivo() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Motivo debe de tener valor");

		// Valida anticipo
		if (solEnt.getAnticipo().compareTo(new BigDecimal(0)) == 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Monto debe ser mayor a 0");

		solEnt.setUsuario(empleado);
		SolicitudViaticosEntity solicitudCreada = solUsPort.crearSolicitud(empleado, solEnt);
		bitacoraPort.ingresarEventoDeSolicitud("Crea solicitud de Viatico", "", solicitudCreada,
				empleadoInfo.getNombre().toUpperCase());
		solicitud.setNumeroSolicitud(String.valueOf(solicitudCreada.getId()));

		return solicitud;

	}

	private SolicitudViaticosEntity solicitudAEntity(Solicitud solicitud, EstatusSolicitudEntity estatus) {

		SolicitudViaticosEntity solEn = new SolicitudViaticosEntity();

		if (!solicitud.getNumeroSolicitud().isEmpty())
			solEn.setId(Integer.valueOf(solicitud.getNumeroSolicitud()));
		solEn.setMotivo(solicitud.getMotivo());
		solEn.setFechaInicio(solicitud.getFechaInicio());
		solEn.setFechaFin(solicitud.getFechaFin());
		solEn.setAnticipo(solicitud.getTotalAnticipo());
		solEn.setUsuario(solicitud.getUsuario());
		solEn.setNombreCompletoUsuario(solicitud.getNombreCompletoUsuario());
		solEn.setEmpresa(solicitud.getEmpresa());
		solEn.setEmpresaDescr(solicitud.getEmpresaDescr());
		solEn.setCeco(solicitud.getCeco());
		solEn.setCecoDescr(solicitud.getCecoDesc());
		solEn.setConcepto("003");
		solEn.setObservaciones(solicitud.getObservaciones());
		solEn.setEstatus(estatus);
		solEn.setFechaCreacion(new Date());
		solEn.setCuentaContable(solicitud.getCuentaContable());
		solEn.setNivel(solicitud.getNivel());
		solEn.setRfc(solicitud.getRfc());

		return solEn;
	}

	@Override
	public void cargaDeComprobanteJPA(String numeroSolicitud, Comprobante comprobante, MultipartFile xml,
			MultipartFile pdf) {

		// MultipartFile to Byte XML
		byte[] xmlB = new byte[0];
		byte[] pdfB = new byte[0];

		if (xml != null && xml.getSize() > 0) {
			try {
				xmlB = xml.getBytes();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (pdf != null && pdf.getSize() > 0) {
			try {
				pdfB = pdf.getBytes();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.info("numero solicitud:" + numeroSolicitud);
		Cfdi cfdi = null;
		boolean conXml = false;

		// Tomar datos de solicitud
		SolicitudViaticosEntity s = solUsPort.obtenerSolicitudJPA(Integer.parseInt(numeroSolicitud));

		// Tomar usuario empleado
		Usuario user = new Usuario();
		user = usuariosPort.encontrarUsuarioIdJPA(s.getUsuario());
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");

		if (user.getNivel() == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"El usuario " + user.getNombre() + " no cuenta con un nivel, contacte al administrador.");

		NivelTopeUsuarioEntity nivel = nivelesPort.obtenerNivelPorNivel(Integer.parseInt(user.getNivel().trim()));
		if (nivel == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel de empleado no encontrado.");

		// Valida estrucdtura
		if (xml != null && xml.getSize() > 0) {
			conXml = true;
			cfdi = cfdiPort.validarCfdi(xmlB);

			// Valida PAC
			if ("pro".equals(ambiente)) {
				try {

					validaCfdi(cfdi);
					GetCFDiStatusResponse response = new GetCFDiStatusResponse();
					response = soapClient.getCfdiStatus(cfdi);
					if (response != null && response.getGetCFDiStatusReturn().getStatusCode().equalsIgnoreCase("602"))
						throw new ApiException(400, response.getGetCFDiStatusReturn().getStatus() + " "
								+ response.getGetCFDiStatusReturn().getStatusCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		if (cfdi != null && !cfdi.getRfcReceptor().equalsIgnoreCase(user.getOrganizaciones().get(0).getRfc()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"RFC Receptor de CFDI y RFC de empresa usuario son diferentes");

		// Validar duplicidad, si existe entonces se manda exception?
		if (conXml) {
			if (cfdiPort.existeCfdiJpa(cfdi.getUuid())) {
				CfdiEntity cfdiE = cfdiPort.obtenerCfdiPorUuid(cfdi.getUuid());
				ComprobanteViaticoEntity cE = compPort
						.obtenerDeComprobante(Integer.valueOf(cfdiE.getId_comprobante_viatico().getId()));
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"UUID ya existe en solicitud " + cE.getNumero_solicitud().getId());
			}
		}

		// Validaciones de montos
		// Total, subTotal, totalImpuestosTrasladados
		if (cfdi != null && comprobante.getSubTotal() != null
				&& cfdi.getSubtotal().compareTo(comprobante.getSubTotal()) != 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subtotal de CFDI y Comprobante no coinciden");

		if (cfdi != null && comprobante.getTotal() != null && cfdi.getTotal().compareTo(comprobante.getTotal()) != 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total de CFDI y Comprobante no coinciden");

		if (conXml) {
			BigDecimal sb = cfdi.getSubtotal();
			// Validar si tiene descuento, en caso que si descontar a subtotal pra la
			// validación
			if (cfdi.getDescuento() != null) {
				sb = sb.subtract(cfdi.getDescuento());
			} else {
				sb = cfdi.getSubtotal();
			}

			System.out.println("Total:" + cfdi.getTotal());
			System.out.println("Subtotal:" + sb);
			System.out.println("Resta total - subtotal:" + cfdi.getTotal().subtract(sb));
			System.out.println("Impuestos:" + comprobante.getImpuesto());

			BigDecimal sbR = cfdi.getTotal().subtract(sb).setScale(2, RoundingMode.DOWN);
			BigDecimal impR = comprobante.getImpuesto().setScale(2, RoundingMode.DOWN);
			System.out.println("Resta total - subtotal, cortado a dos decimales:" + sbR);
			System.out.println("Impuestos cortado a dos decimales:" + impR);
			BigDecimal diferencia = sbR.subtract(impR).abs();
			System.out.println("Diferencia:" + diferencia);

			boolean mostrarErrorImportes = false;

			// Validar primero impuestos que tiene el ish en el subtotal
			if (cfdi != null && cfdi.getTotal() != null && cfdi.getSubtotal() != null && (sbR).compareTo(impR) != 0) {
				mostrarErrorImportes = true;
			}

			// Validar tolerancia de 1 arriba o abajo
			if (diferencia.compareTo(BigDecimal.ONE) == 1) {
				System.out.println("Tiene diferencia");
			} else {
				System.out.println("Se tiene diferencia de 1 pero se pasa");
				mostrarErrorImportes = false;
			}

			// Validación de impuestos retenidos
			if (mostrarErrorImportes) {

				if (cfdi.getTotalImpuestosRetenidos() == null) {
					System.out.println("No se encontro nodo impuestos retenidos");
					cfdi.setTotalImpuestosRetenidos(BigDecimal.ZERO);

				}

				impR = impR.subtract(cfdi.getTotalImpuestosRetenidos());

				System.out.println("*---*");
				System.out.println("Restara el IMP. RET. de impuestos para validacion");
				System.out.println("Resta total - subtotal - impret, cortado a dos decimales:" + sbR);
				System.out.println("Impuestos cortado a dos decimales:" + impR);

				if (cfdi != null && cfdi.getTotal() != null && cfdi.getSubtotal() != null
						&& (sbR).compareTo(impR) != 0) {
					mostrarErrorImportes = true;
				} else {
					mostrarErrorImportes = false;
				}
			}

			if (mostrarErrorImportes) {
				// Validar primero impuestos que tiene el ish en el subtotal
				if (cfdi != null && cfdi.getTotal() != null && cfdi.getSubtotal() != null
						&& (sbR).compareTo(impR) != 0) {
					mostrarErrorImportes = true;
				} else {
					mostrarErrorImportes = false;
				}
			}

			// Validación de impuesto ISH
			if (mostrarErrorImportes) {

				if (cfdi.getIshTrasladado() == null) {
					System.out.println("No se encontro ISH trasladado");
					cfdi.setIshTrasladado(BigDecimal.ZERO);

				}

				impR = impR.subtract(cfdi.getIshTrasladado());

				System.out.println("*---*");
				System.out.println("Restara el ISH de impuestos para validación");
				System.out.println("Resta total - subtotal - ish, cortado a dos decimales:" + sbR);
				System.out.println("Impuestos cortado a dos decimales:" + impR);

				if (cfdi != null && cfdi.getTotal() != null && cfdi.getSubtotal() != null
						&& (sbR).compareTo(impR) != 0) {
					mostrarErrorImportes = true;
				} else {
					mostrarErrorImportes = false;
				}
			}

			if (mostrarErrorImportes) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Total impuestos trasladados de CFDI y total impuestos trasladados de Comprobante no coinciden");
			} else {
				System.out.print("Paso validaciones de ISH");
			}

		}

		ConfiguracionEntity configuracionDias = new ConfiguracionEntity();
		Calendar cal = Calendar.getInstance();
		cal.setTime(s.getFechaFin());

		configuracionDias = configuracionPort.obtenerConfiguracion(1);
		cal.add(Calendar.DATE, Integer.valueOf(configuracionDias.getValor1()));
		Date fechaSumada = cal.getTime();

		if (comprobanteFueraFecha(comprobante.getFecha(), s.getFechaInicio(), s.getFechaFin(), fechaSumada)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Fecha de carga de comprobante se encuentra fuera de rango de fechas de solicitud.");
		}

//		if (!comprobante.getFecha().after(s.getFechaInicio()) && !comprobante.getFecha().equals(s.getFechaInicio())
//				|| comprobante.getFecha().after(fechaSumada))
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//					"Fecha de carga de comprobante se encuentra fuera de rango de fechas de solicitud.");

		// Que la cuenta contable exista
		// Obtiene datos de subcuentaContable
		SubCuentaContableEntity subCuenta = subPort
				.obtenerSubcuentaContable(Integer.valueOf(comprobante.getSubCuenta().getId()));
		if (subCuenta == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe cuenta contable");

		validaComprobantesPorDia(nivel, s, comprobante, subCuenta);

		// Formar ruta de archivos;
		String uuid = UUID.randomUUID().toString();
		String rutaParaArchivosBD = archivosPort.generaRutaArchivo(UtilidadesAdapter.tomarAnioActual(), s.getUsuario(),
				numeroSolicitud);
		String nombreXml = "/" + uuid + ".xml";
		String nombrePdf = "/" + uuid + ".pdf";
		String descripcion = "";

		// Guardar archivos XML y PDF
		if (xmlB.length > 0) {
			System.out.print("Cargando XML");
			archivosPort.guardarArchivo(xmlB, rutaParaArchivosBD, nombreXml);
			comprobante.setRutaXml(rutaParaArchivosBD + nombreXml);

			String serie = cfdi.getSerie() != null ? cfdi.getSerie() : "";
			String folio = cfdi.getFolio() != null ? cfdi.getFolio() : "";
			String referencia = "";
			referencia = String.join(",", serie, folio);
			descripcion = "XML por " + cfdi.getTotal() + ", referencia: " + referencia + ". ";
		}

		if (pdfB.length > 0) {
			System.out.print("Cargando PDF");
			archivosPort.guardarArchivo(pdfB, rutaParaArchivosBD, nombrePdf);
			comprobante.setRutaPdf(rutaParaArchivosBD + nombrePdf);

			descripcion = descripcion + "PDF por " + comprobante.getTotal();
		}

		ComprobanteViaticoEntity c = new ComprobanteViaticoEntity();
		CfdiEntity cfdiEn = new CfdiEntity();
		ComprobanteViaticoEntity comprobanteReturn = new ComprobanteViaticoEntity();

		c = modelToEntityComprobante(comprobante, subCuenta, s);
		// Guardar comprobante
		comprobanteReturn = compPort.cargaDeComprobante(c);

		if (cfdi != null) {
			cfdiEn = modelToEntityCfdi(cfdi, comprobanteReturn);
			cfdiPort.guardaCfdi(cfdiEn);
		}

		// Escribir registro en envento viatico
		// EventoViaticoEntity eventoEntity = new EventoViaticoEntity();
		bitacoraPort.ingresarEventoDeSolicitud("Carga de comprobante", descripcion, s, user.getNombre().toUpperCase());

	}

	private void validaComprobantesPorDia(NivelTopeUsuarioEntity nivel, SolicitudViaticosEntity solicitud,
			Comprobante comprobante, SubCuentaContableEntity subCuenta) {

		List<ComprobanteViaticoEntity> comprobantes = new ArrayList<ComprobanteViaticoEntity>();
		BigDecimal totalNoAplica = BigDecimal.ZERO;
		BigDecimal totalPermitidoAlimentoPorDia = BigDecimal.ZERO;
		comprobantes = compPort.obtenerComprobantesPorDiaYTipogasto(comprobante.getFecha(), solicitud,
				subCuenta.getDescripcion());

		totalNoAplica = compPort.obtenerPropinaComprobantesPorDiaYTipogasto(comprobante.getFecha(), solicitud,
				subCuenta.getDescripcion());
		BigDecimal sumatoria = new BigDecimal(0);
		BigDecimal resultado = new BigDecimal(0);

		for (ComprobanteViaticoEntity c : comprobantes) {

			// Suma comprobantes anteriores
			sumatoria = sumatoria.add(c.getTotal());
		}

		// Suma total de comprobantes incluyendo el que se va a insertar
		sumatoria = sumatoria.add(comprobante.getTotal());

		// BigDecimal validacionNA = BigDecimal.ZERO;
		// BigDecimal noAplicaComp = BigDecimal.ZERO;
		BigDecimal total = comprobante.getTotal();
		if (subCuenta.getDescripcion().equalsIgnoreCase("desayuno")) {
			totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosDesayunoDia();
			if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
				resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
			}
		}
		if (subCuenta.getDescripcion().equalsIgnoreCase("comida")) {
			totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosComidaDia();
			if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
				resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
			}
		}
		if (subCuenta.getDescripcion().equalsIgnoreCase("cena")) {
			totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosCenaDia();
			if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
				resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
			}
		}

		System.out.println("total dia: " + sumatoria);
		System.out.println("resultado: " + resultado);

		if (resultado.compareTo(BigDecimal.ZERO) > 0)
			comprobante.setNoAplica(resultado.abs());
		else
			comprobante.setNoAplica(new BigDecimal("0.00"));

		System.out.println(comprobante.getNoAplica());

	}

	@Override
	public void recalculoNoAplica(int numeroSolicitud) {

		System.out.println("Solicitud:" + numeroSolicitud);

		// Tomar datos de solicitud
		SolicitudViaticosEntity s = solUsPort.obtenerSolicitudJPA(numeroSolicitud);

		System.out.println("Usuario solicitud:" + s.getUsuario());
		System.out.println("Nivel solicitud:" + s.getNivel());
		// Tomar usuario empleado
		Usuario user = new Usuario();
		user = usuariosPort.encontrarUsuarioIdJPA(s.getUsuario());

		// Limpiar el no aplica
		compPort.actualizaNoAplicaACero(numeroSolicitud);

		// Tomar nivel
		if (user.getNivel() != null) {
			NivelTopeUsuarioEntity nivel = nivelesPort.obtenerNivelPorNivel(Integer.parseInt(user.getNivel().trim()));
			List<ComprobanteViaticoEntity> comprobantesSol = s.getComprobanteViaticosEntity();

			StringBuilder sb = new StringBuilder();

			for (ComprobanteViaticoEntity comp : comprobantesSol) {

				System.out.println("----------");
				String desSubCuenta = comp.getSub_cuenta_contable().getDescripcion();

				List<ComprobanteViaticoEntity> comprobantes = new ArrayList<ComprobanteViaticoEntity>();
				BigDecimal totalNoAplica = BigDecimal.ZERO;
				BigDecimal totalPermitidoAlimentoPorDia = BigDecimal.ZERO;
				comprobantes = compPort.obtenerComprobantesPorDiaYTipogasto(comp.getFechaCarga(), s, desSubCuenta);

				totalNoAplica = compPort.obtenerPropinaComprobantesPorDiaYTipogasto(comp.getFechaCarga(), s,
						desSubCuenta);
				BigDecimal sumatoria = new BigDecimal(0);
				BigDecimal resultado = new BigDecimal(0);

				for (ComprobanteViaticoEntity c : comprobantes) {

					// Suma comprobantes anteriores
					sumatoria = sumatoria.add(c.getTotal());
				}

				// Suma total de comprobantes incluyendo el que se va a insertar
				sumatoria = sumatoria.add(comp.getTotal());

				BigDecimal total = comp.getTotal();
				if (desSubCuenta.equalsIgnoreCase("desayuno")) {
					totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosDesayunoDia();
					if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
						resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
					}
					System.out.println("----------");
				}
				if (desSubCuenta.equalsIgnoreCase("comida")) {
					totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosComidaDia();
					if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
						resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
					}
					System.out.println("----------");
				}
				if (desSubCuenta.equalsIgnoreCase("cena")) {
					totalPermitidoAlimentoPorDia = nivel.getTotalAlimentosCenaDia();
					if (sumatoria.compareTo(totalPermitidoAlimentoPorDia) == 1) {
						resultado = montoNoAplica(totalNoAplica, total, totalPermitidoAlimentoPorDia);
					}
					System.out.println("----------");
				}

				compPort.actualizaMontoNoAplica(comp.getId(), resultado);
				// Monto no aplica
				sb.append("Monto no aplica para comp. " + comp.getId() + ":" + resultado + "\n");

			}

			if (sb.length() != 0) {
				System.out.println(sb.toString());
			}
		} else {
			System.out.println("No se tiene nivel al usuario");
		}

	}

	@Override
	public void recalculoISH(int numeroSolicitud) {

		System.out.println("Solicitud:" + numeroSolicitud);

		// Tomar datos de solicitud
		SolicitudViaticosEntity s = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		List<ComprobanteViaticoEntity> comprobantesSol = s.getComprobanteViaticosEntity();

		Cfdi cfdi = null;

		for (ComprobanteViaticoEntity comp : comprobantesSol) {

			if (!comp.getRutaXml().equals("")) {
				// Tomar ruta
				String ruta = comp.getRutaXml();
				byte[] xml = null;
				xml = archivosPort.obtenerArchivo(ruta);
				cfdi = cfdiPort.validarCfdi(xml);
				System.out.println(cfdi.getIshTrasladado());

				BigDecimal ish = cfdi.getIshTrasladado();
				if (ish == null) {
					ish = BigDecimal.ZERO;
				}

				cfdiPort.actualizarISH(comp.getCfdiEntity().getIdCfdi(), ish);

			}

		}

	}

	public BigDecimal montoNoAplica(BigDecimal totalNoAplica, BigDecimal total,
			BigDecimal totalPermitidoAlimentoPorDia) {
		BigDecimal validacionNA = BigDecimal.ZERO;
		BigDecimal noAplicaComp = BigDecimal.ZERO;
		BigDecimal resultado = BigDecimal.ZERO;

		// Se paso del limite de su nivel
		validacionNA = totalPermitidoAlimentoPorDia.subtract(totalNoAplica);
		System.out.println("validacionNA:" + validacionNA);
		noAplicaComp = total.subtract(validacionNA);
		int negativo = noAplicaComp.signum();
		System.out.println("negativo?" + negativo);
		System.out.println("noAplicaComp:" + noAplicaComp);
		System.out.println("val:" + noAplicaComp.compareTo(total));
		if (negativo != -1) {
			if (noAplicaComp.compareTo(total) == -1) {
				resultado = noAplicaComp;
			} else {
				resultado = total;

			}
		} else {
			resultado = BigDecimal.ZERO;
		}
		System.out.println(resultado);
		System.out.println("valida res:" + resultado.compareTo(BigDecimal.ZERO));
		if (resultado.compareTo(BigDecimal.ZERO) > 0) {
			System.out.println("Si tuvo valor:" + resultado.abs());
		} else {
			System.out.println("Fue 0:" + new BigDecimal("0.00"));
		}
		return resultado;
	}

	private boolean validaCfdi(Cfdi cfdi) throws IOException {

		/*
		 * URL url = new URL(urlSoap); StringBuilder sb = new StringBuilder();
		 * 
		 * HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		 * connection.setRequestMethod("POST");
		 * connection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
		 * connection.setRequestProperty("SOAPAction", "");
		 * 
		 * sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); sb.append(
		 * "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cfdi=\"http://cfdi.service.ediwinws.edicom.com\">"
		 * ); sb.append("<soapenv:Header/>"); sb.append("<soapenv:Body>");
		 * sb.append("<cfdi:getCFDiStatus>"); sb.append("<cfdi:user>");
		 * sb.append(userSoap); sb.append("</cfdi:user>"); sb.append("<cfdi:password>");
		 * sb.append(passSoap); sb.append("</cfdi:password>"); sb.append("<cfdi:rfcE>");
		 * sb.append(cfdi.getRfcEmisor()); sb.append("</cfdi:rfcE>");
		 * sb.append("<cfdi:rfcR>"); sb.append(cfdi.getRfcReceptor());
		 * sb.append("</cfdi:rfcR>"); sb.append("<cfdi:uuid>");
		 * sb.append(cfdi.getUuid()); sb.append("</cfdi:uuid>");
		 * sb.append("<cfdi:total>"); sb.append(cfdi.getTotal());
		 * sb.append("</cfdi:total>"); sb.append("<cfdi:test>"); sb.append(false);
		 * sb.append("</cfdi:test>"); sb.append("</cfdi:getCFDiStatus>");
		 * sb.append("</soapenv:Body>"); sb.append("</soapenv:Envelope>");
		 * 
		 * connection.setDoOutput(true);
		 * 
		 * OutputStreamWriter oSW = new
		 * OutputStreamWriter(connection.getOutputStream()); oSW.write(sb.toString());
		 * oSW.flush();
		 * 
		 * int codigoRespuesta = Integer.valueOf(connection.getResponseCode());
		 * System.out.println("Entrada" + sb.toString());
		 * System.out.println(codigoRespuesta);
		 * 
		 * BufferedReader rd;
		 * 
		 * if (codigoRespuesta >= 400) {
		 * 
		 * rd = new BufferedReader(new InputStreamReader(connection.getErrorStream(),
		 * "UTF-8"));
		 * 
		 * } else {
		 * 
		 * rd = new BufferedReader(new InputStreamReader(connection.getInputStream(),
		 * "UTF-8"));
		 * 
		 * }
		 * 
		 * String line; StringBuilder salida = new StringBuilder(); while ((line =
		 * rd.readLine()) != null) { salida.append(line); }
		 * 
		 * System.out.println(salida.toString());
		 */
		return false;
	}

	private CfdiEntity modelToEntityCfdi(Cfdi cfdiIn, ComprobanteViaticoEntity comprobante) {
		CfdiEntity cfdi = new CfdiEntity();

		cfdi.setMoneda(cfdiIn.getMoneda());
		cfdi.setFecha(cfdiIn.getFecha());

		// Traslados

		if (cfdiIn.getIvaTrasladado() != null)
			cfdi.setIva(cfdiIn.getIvaTrasladado().floatValue());

//		if (cfdiIn.getIsrTrasladado() != null)
//			cfdi.setIsr(cfdiIn.getIsrRetenido().floatValue());
//
		if (cfdiIn.getIshTrasladado() != null)
			cfdi.setIsh(cfdiIn.getIshTrasladado().floatValue());

		if (cfdiIn.getIepsTrasladado() != null)
			cfdi.setIeps(cfdiIn.getIepsTrasladado().floatValue());

		// Retenciones
		/*
		 * if (cfdiIn.getIvaRetenido() != null)
		 * cfdi.setIvaRetenido(cfdiIn.getIvaRetenido().floatValue());
		 * 
		 * if (cfdiIn.getIsrRetenido() != null)
		 * cfdi.setIsrRetenido(cfdiIn.getIsrRetenido().floatValue());
		 * 
		 * if (cfdiIn.getIepsRetenido() != null)
		 * cfdi.setIepsRetenido(cfdiIn.getIepsRetenido().floatValue());
		 */

		// if (cfdiIn.getIvaTrasladado() != null)
		cfdi.setTua(1);

		if (cfdiIn.getSubtotal() != null)
			cfdi.setSubtotal(cfdiIn.getSubtotal().floatValue());

		if (cfdiIn.getTotal() != null)
			cfdi.setTotal(cfdiIn.getTotal().floatValue());

		String serie = cfdiIn.getSerie() != null ? cfdiIn.getSerie() : "";
		String folio = cfdiIn.getFolio() != null ? cfdiIn.getFolio() : "";

		cfdi.setNumeroFactura(serie + "-" + folio);
		cfdi.setRfcEmisor(cfdiIn.getRfcEmisor());
		cfdi.setRfcReceptor(cfdiIn.getRfcReceptor());
		cfdi.setNombreEmisor(cfdiIn.getNombreEmisor());
		cfdi.setUuid(cfdiIn.getUuid());
		cfdi.setId_comprobante_viatico(comprobante);
		cfdi.setSerie(serie);
		cfdi.setFolio(folio);
		cfdi.setMetodoPago(cfdiIn.getMetodoPago());
		cfdi.setFormaPago(cfdiIn.getFormaPago());
		return cfdi;
	}

	private ComprobanteViaticoEntity modelToEntityComprobante(Comprobante comprobante,
			SubCuentaContableEntity subCuenta, SolicitudViaticosEntity solicitud) {

		ComprobanteViaticoEntity c = new ComprobanteViaticoEntity();

		if (comprobante.getIdComprobanteViatico() > 0)
			c.setId(comprobante.getIdComprobanteViatico());

		c.setImpuestos(comprobante.getImpuesto());
		c.setSubTotal(comprobante.getSubTotal());
		c.setTotal(comprobante.getTotal());
		c.setFechaCarga(comprobante.getFecha());
		c.setSub_cuenta_contable(subCuenta);
		c.setAprobacionContador(comprobante.isAprobacionContador());
		c.setAprobacionGerente(comprobante.isAprobacionGerente());
		c.setAprobacionPrestador(comprobante.isAprobacionPrestador());
		c.setEstatusComprobante(comprobante.getEstatusComprobante());
		c.setRutaXml(comprobante.getRutaXml());
		c.setRutaPdf(comprobante.getRutaPdf());
		c.setObservaciones(comprobante.getObservaciones());
		c.setNumero_solicitud(solicitud);
		c.setTipoGasto(subCuenta.getDescripcion().toLowerCase());

		BigDecimal noAplicaValor = null;
		noAplicaValor = comprobante.getNoAplica() != null ? comprobante.getNoAplica() : new BigDecimal("0.00");
		c.setNoAplica(noAplicaValor);

		c.setAprobacionNoAplica(comprobante.isAprobacionNoAplica());

		BigDecimal propina = null;
		propina = comprobante.getPropina() != null ? comprobante.getPropina() : new BigDecimal("0.00");
		c.setPropina(propina);
		return c;
	}

	@Override
	public void eliminarSolicitud(int solicitud) {

		SolicitudViaticosEntity solEntity = new SolicitudViaticosEntity();

		solEntity = solUsPort.obtenerSolicitudJPA(solicitud);

		if (solEntity.getEstatus().getId() == 1)
			solUsPort.eliminarSolicitud(solEntity);

	}

	@Override
	public void editarSolicitud(Solicitud solicitud) {

		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();

		estatusEntity = estatusPort.obtieneEstatusSolicitud(Integer.valueOf(solicitud.getEstatus()));

		solicitudEntity = solicitudAEntity(solicitud, estatusEntity); // solicitudEstatusEntity(solicitud,
																		// estatusEntity);

		solUsPort.editarSolicitud(solicitudEntity);

	}

	@Override
	public void enviaEstatusAprobacionContador(int solicitud, int estatus) {

		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();
		List<String> emailUsers = new ArrayList<String>();

		estatusEntity = estatusPort.obtieneEstatusSolicitud(estatus);
		solUsPort.enviaPeticionAceptacion(solicitud, estatusEntity);

		solicitudEntity = solUsPort.obtenerSolicitudJPA(solicitud);

		bitacoraPort.ingresarEventoDeSolicitud("Solicitud enviada a aprobacion", "", solicitudEntity,
				solicitudEntity.getNombreCompletoUsuario().toUpperCase());

		// Obtener usuarios por empresa de empleado
		List<UsuarioEntity> users = new ArrayList<UsuarioEntity>();
		users = usuariosPort.encontrarUsuarioTempusPorEmpresa(solicitudEntity.getEmpresa(), "contabilidad");

		String email = null;

		for (UsuarioEntity u : users) {
			emailUsers.add(u.getUsEmail());

		}

		// emailUsers.add("humbertogarciag7@gmaill.com");
		email = StringUtils.join(emailUsers, ",");

		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "a quien corresponda");
		params.put("${usuario-solicitante}", solicitudEntity.getNombreCompletoUsuario());
		params.put("${numero-solicitud}", solicitud);

		byte[] bytePdf = null;

		try {
			bytePdf = pdfSolicitudViatico.generarPdfSolicitud(obtenerSolicitudJPA(solicitud));
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}

		envioCorreoPort.enviarCorreoSolicitudAprobacionSolicitante(email, "", params, bytePdf);

	}

	@Override
	public void solicitaAprobacionDeComprobante(int comprobante, String estatus) {

		ComprobanteViaticoEntity cEnt = new ComprobanteViaticoEntity();
		// EventoViaticoEntity evento = new EventoViaticoEntity();

		cEnt = compPort.obtenerDeComprobante(comprobante);
		compPort.solicitaAprobacionDeComprobanteEstatus(comprobante, estatus);

		// evento = this.eventoViatico("CPA", cEnt.getNumero_solicitud(), "Comprobante
		// pendiente aprobacion", cEnt.getNumero_solicitud().getUsuario());
		// solUsPort.crearEventoSolicitud(evento);
		bitacoraPort.ingresarEventoDeSolicitud("Comprobante pendiente aprobacion", "", cEnt.getNumero_solicitud(),
				cEnt.getNumero_solicitud().getNombreCompletoUsuario().toUpperCase());

	}

	@Override
	public void modificarComprobante(int idComprobante, Comprobante comprobante, String aprobacionAplica,
			String usuario) {

		ComprobanteViaticoEntity comprobanteEntity = new ComprobanteViaticoEntity();
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();
		SubCuentaContableEntity subCuenta = new SubCuentaContableEntity();
		Usuario user = new Usuario();

		comprobanteEntity = compPort.obtenerDeComprobante(idComprobante);
		if (comprobanteEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe comprobante " + idComprobante);

		solicitudEntity = solUsPort.obtenerSolicitudJPA(comprobanteEntity.getNumero_solicitud().getId());
		if (solicitudEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe solicitud.");

		user = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe usuario " + usuario);

		if (aprobacionAplica != null && usuario != null) {
			// Actualiza boolean en comprobante

			switch (aprobacionAplica) {
			case "0":
				compPort.actualizaAprobacionNoAplica(idComprobante, false);
				bitacoraPort.ingresarEventoDeSolicitud("Descuento deshabilitado", "Comprobante: " + idComprobante,
						solicitudEntity, user.getNombre());
				break;

			case "1":
				compPort.actualizaAprobacionNoAplica(idComprobante, true);
				bitacoraPort.ingresarEventoDeSolicitud("Descuento habilitado", "Comprobante: " + idComprobante,
						solicitudEntity, user.getNombre());

				break;
			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Es necesario ingresar el valor de actualización para el comprobante.");
			}

		} else {

			subCuenta = subPort.obtenerSubcuentaContable(Integer.valueOf(comprobante.getSubCuenta().getId()));
			if (subCuenta == null)
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe cuenta contable");

			ComprobanteViaticoEntity comprobanteReturn = new ComprobanteViaticoEntity();

			comprobanteReturn = modelToEntityComprobante(comprobante, subCuenta, solicitudEntity);
			compPort.modificarDeComprobante(comprobanteReturn);

			bitacoraPort.ingresarEventoDeSolicitud("Modifica comprobante", "", solicitudEntity,
					solicitudEntity.getNombreCompletoUsuario().toUpperCase());
		}

	}

	@Override
	public void modificarMontoAprobado(int idComprobante, BigDecimal montoAprobado, String usuario) {

		ComprobanteViaticoEntity comprobanteEntity = new ComprobanteViaticoEntity();
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();
		Usuario user = new Usuario();

		comprobanteEntity = compPort.obtenerDeComprobante(idComprobante);
		if (comprobanteEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe comprobante " + idComprobante);

		solicitudEntity = solUsPort.obtenerSolicitudJPA(comprobanteEntity.getNumero_solicitud().getId());
		if (solicitudEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe solicitud.");

		user = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (user == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe usuario " + usuario);

		if (montoAprobado != null && usuario != null) {

			compPort.actualizaMontoADescontar(idComprobante, montoAprobado);
			bitacoraPort.ingresarEventoDeSolicitud("Se agrego monto aprobado: " + montoAprobado,
					"Comprobante: " + idComprobante, solicitudEntity, user.getNombre());

		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario ingresar el monto aprobado.");
		}

	}

	@Override
	public List<Solicitud> obtenerSolicitudesReporte(String estatus, String empresas, String fechaInicio,
			String fechaFin, String evento, int numeroSolicitud) {

		if (numeroSolicitud != 0) {
			log.info("Obteniendo solo solicitud por numero solicitud");
			return solUsPort.obtenerSoloSolicitudPorNumSolicitud(numeroSolicitud);
		}

		if (evento == null)
			evento = "";

		if ("".equals(evento)) {

			log.info("Obtener solicitudes por fecha y estatus");
			if (estatus.equals("")) {
				log.info("Obtener estatus");
				StringBuilder sb = new StringBuilder();
				List<EstatusSolicitudEntity> estatuses = estatusPort.obtenerEstatus();
				for (EstatusSolicitudEntity est : estatuses) {
					sb.append(est.getId() + ",");
				}
				if (sb.toString().length() > 0) {
					estatus = sb.toString().substring(0, sb.toString().length() - 1);
				}
			}

			List<Solicitud> solicitudes = solUsPort.obtenerSolicitudesReporte(estatus, empresas, fechaInicio, fechaFin,
					numeroSolicitud);
			for (Solicitud solicitud : solicitudes) {
				BigDecimal totalComprobado = cfdiPort
						.totalComprobado(Integer.valueOf(solicitud.getNumeroSolicitud())) != null
								? cfdiPort.totalComprobado(Integer.valueOf(solicitud.getNumeroSolicitud()))
								: BigDecimal.ZERO;
				solicitud.setTotalComprobado(totalComprobado);
			}
			return solicitudes;
		} else {
			log.info("Obtener solicitudes por evento y estatus");
			return solUsPort.obtenerPorEventoYFechas(evento, fechaInicio, fechaFin);
		}

	}

	@Override
	public List<Solicitud> obtenerSolicitudesReporteDirector(String estatus, String empresas, String fechaInicio,
			String fechaFin) {

		if (estatus.equals("")) {
			StringBuilder sb = new StringBuilder();
			List<EstatusSolicitudEntity> estatuses = estatusPort.obtenerEstatus();
			for (EstatusSolicitudEntity est : estatuses) {
				sb.append(est.getId() + ",");
			}
			if (sb.toString().length() > 0) {
				estatus = sb.toString().substring(0, sb.toString().length() - 1);
			}
		}

		List<Solicitud> solicitudes = solUsPort.obtenerSolicitudesReporteDirector(estatus, empresas, fechaInicio,
				fechaFin);
		for (Solicitud solicitud : solicitudes) {
			BigDecimal totalComprobado = cfdiPort
					.totalComprobado(Integer.valueOf(solicitud.getNumeroSolicitud())) != null
							? cfdiPort.totalComprobado(Integer.valueOf(solicitud.getNumeroSolicitud()))
							: BigDecimal.ZERO;
			solicitud.setTotalComprobado(totalComprobado);
		}
		return solicitudes;

	}

	@Override
	public List<Solicitud> obtenerSolicitudesPorEmpresasEstatus(String empresas, String estatus, String usuario) {

		String[] estatusArr = estatus.split(",");
		String[] empresasArr = empresas.split(",");

		List<Integer> estatusInt = new ArrayList<Integer>();
		List<String> empresasStr = new ArrayList<String>();
		List<SolicitudViaticosEntity> solicitudEnt = new ArrayList<SolicitudViaticosEntity>();
		List<Solicitud> solicitud = new ArrayList<Solicitud>();
		List<String> empleadosStr = new ArrayList<String>();
		List<Usuario> empleadoInfo = new ArrayList<Usuario>();
		Usuario user = new Usuario();

		for (String e : estatusArr) {
			estatusInt.add(Integer.valueOf(e));
		}

		for (String em : empresasArr) {
			empresasStr.add(em);
		}

		user = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		String rol = "";

		for (RolModel r : user.getRol()) {
			if (r.getDescripcion().equalsIgnoreCase("director"))
				rol = r.getDescripcion();
		}

		solicitudEnt = solUsPort.obtenerSolicitudesPorEmpresasYEstatus(estatusInt, empresasStr, rol);

		for (SolicitudViaticosEntity s : solicitudEnt) {

			empleadosStr.add(s.getUsuario());

		}

		empleadoInfo = usuariosPort.encontrarEmpleados(empleadosStr);

		for (SolicitudViaticosEntity s : solicitudEnt) {

			for (Usuario u : empleadoInfo) {
				if (u.getUsuario().contains(s.getUsuario())) {
					user = u;
					break;
				}
			}

			solicitud.add(solicitud(s, user));
		}

		return solicitud;
	}

	@Override
	public void validaCfdi(MultipartFile xml) {
		// // MultipartFile to Byte XML

		byte[] xmlB = new byte[0];
		Cfdi cfdi = null;
		// boolean conXml = false;

		if (xml != null && xml.getSize() > 0) {
			try {
				xmlB = xml.getBytes();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Valida estrucdtura
		if (xml != null && xml.getSize() > 0) {
			// conXml = true;
			cfdi = cfdiPort.validarCfdi(xmlB);
			if (cfdi == null || !cfdi.isValida())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cfdi tiene una estructura invalida.");
		}

	}

	public static boolean comprobanteFueraFecha(Date fechaComprobante, Date fechaInicio, Date fechaFin,
			Date fechaSumada) {
		boolean salida = false;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		System.out.println("Fecha inicio:          " + dateFormat.format(fechaInicio));
		System.out.println("Fecha fin:             " + dateFormat.format(fechaFin));
		System.out.println("Fecha sumada:          " + dateFormat.format(fechaSumada));
		System.out.println("Fecha comprobante:     " + dateFormat.format(fechaComprobante));
		System.out.println("----------------------------");
		// System.out.println("Fecha comprobante es despues de fecha fin:" +
		// fechaComprobante.after(fechaFin));
		// System.out.println("Fecha comprobante es igual a fecha fin:" +
		// fechaComprobante.equals(fechaFin));
		System.out.println("Fecha comprobante es despues de fecha sumada:" + fechaComprobante.after(fechaSumada));

		// if (!fechaComprobante.after(fechaFin) && !fechaComprobante.equals(fechaFin)
		// || fechaComprobante.after(fechaSumada)) { salida = true; }

		System.out.println("Fecha sumada:          " + fechaSumada);
		System.out.println("Fecha comprobante:     " + fechaComprobante);
		if (fechaComprobante.after(fechaSumada)) {
			salida = true;
		}

		return salida;
	}
	
	public void altaDispercionParaPruebas(int idSolicitud) {
		SolicitudViaticosEntity solicitud =solUsPort.obtenerSolicitudJPA(idSolicitud);
		solicitud.setNombreCompletoUsuario("JOB");
		bitacoraPort.ingresarEventoDeSolicitud(estatusCargaEntrega, "", solicitud,
				solicitud.getNombreCompletoUsuario().toUpperCase());
	}

	public static void main(String args[]) throws ParseException {

		DateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd");
		dateformat2.setTimeZone(TimeZone.getTimeZone("UTC"));
		String fis = "2022-02-21";
		String ffs = "2022-02-28";
		String fcs = "2022-03-15";

		Date fi = dateformat2.parse(fis);
		Date ff = dateformat2.parse(ffs);
		Date fc = dateformat2.parse(fcs);

		System.out.println("Date fecha fin:" + ff);

		Calendar cal = Calendar.getInstance();
		cal.setTime(ff);
		cal.add(Calendar.DATE, Integer.valueOf("17"));
		Date fechaSumada = cal.getTime();

		System.out.println("Date fecha sumada:" + fechaSumada);

		if (ViaticosDeUsuarioService.comprobanteFueraFecha(fc, fi, ff, fechaSumada)) {
			System.out.println("Fecha de carga de comprobante se encuentra fuera de rango de fechas de solicitud.");
		}

	}

	@Override
	public void actualizaEstatusSolicitud(int numeroSolicitud, int estatus) {
		SolicitudViaticosEntity s = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		EstatusSolicitudEntity e = estatusPort.obtieneEstatusSolicitud(estatus);
		s.setEstatus(e);
		solUsPort.editarSolicitud(s);
	}

}
