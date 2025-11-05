package com.viaticos.adapter.out.file;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.Cfdi;
import com.viaticos.domain.Comprobante;
import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
public class PdfSolicitudViatico {

	static Logger log = LoggerFactory.getLogger(PdfSolicitudViatico.class);

	boolean local = false;

	private BigDecimal TotalMenosNoAplica;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EventoDeViaticoPort eventoPort;

	@Autowired
	CfdiPort cfdiPort;

	@Value("${logo}")
	private static String rutaLogo;

	private static String rutaPdfLocal = "C:/SolicitudDeViatico.pdf";

	private static final Font normal = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Light.ttf", 8,
			Font.NORMAL);
	private static final Font negrita = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 8, Font.BOLD);
	private static final Font normalTabla = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Light.ttf", 6,
			Font.NORMAL);
	private static final Font negritaTabla = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 6,
			Font.BOLD);
	private static final Font negritaTitulos = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 6,
			Font.BOLD, BaseColor.WHITE);
	private static final Font negritaTitulo = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 12,
			Font.BOLD);

	public static void main(String args[]) {

		PdfSolicitudViatico p = new PdfSolicitudViatico();
		try {
			try {

				Solicitud s = new Solicitud();
				s.setNumeroSolicitud("233");
				s.setFechaInicio(new Date());
				s.setFechaFin(new Date());
				s.setObservaciones("Lorem ipsum dolor sit amet consectetur adipiscing elit dui aliquet erat praesent ornare, viverra scelerisque auctor volutpat nunc felis maecenas rhoncus ultricies cras. Rutrum praesent ad donec turpis suspendisse lacus magnis bibendum primis curae hac accumsan conubia, ridiculus sem felis euismod magna iaculis faucibus eleifend vel sed aenean suscipit. Volutpat egestas parturient lacus duis penatibus etiam tortor nunc, senectus potenti rutrum vivamus ad felis morbi malesuada scelerisque, habitant magnis viverra fermentum porta inceptos ac.");
				s.setEstatus("Pendiente");
				s.setConcepto("003");
				s.setTotalAnticipo(new BigDecimal("2000"));
				s.setCecoDesc("CECO DESC");
				s.setMotivo("TEST");
				s.setEstatusDescripcion("ESTATUS");

				Usuario u = new Usuario();
				u.setNombre("Alfonso Medina");
				s.setUsuarioObj(u);

				List<Comprobante> l = new ArrayList<Comprobante>();
				for (int i = 0; i < 14; i++) {
					Comprobante c = null;
					c = new Comprobante();
					Cfdi cfdi = new Cfdi();
					cfdi.setFecha(new Date());
					cfdi.setNombreEmisor("RESTAURANTE LA ESPERANZA");
					cfdi.setRfcEmisor("XXXXXXXXXXX");
					cfdi.setSerie("F");
					cfdi.setFolio("122");
					cfdi.setIvaRetenido(new BigDecimal("0.00"));
					cfdi.setIsrRetenido(new BigDecimal("0.00"));
					cfdi.setIepsRetenido(new BigDecimal("0.00"));
					cfdi.setSubtotal(new BigDecimal("100.00"));
					cfdi.setTotal(new BigDecimal("1100.00"));
					c.setNoAplica(new BigDecimal("234"));
					c.setObservaciones("Todo bien");
					c.setSubCuentaContable("54-3-4");
					c.setCfdi(cfdi);
					l.add(c);
				}

				s.setComprobantes(l);

				p.generarPdfSolicitud(s);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public byte[] generarPdfSolicitud(Solicitud solicitud) throws FileNotFoundException, DocumentException {

		// Obtener datos de usuario
		Usuario user = new Usuario();

		// Busca en tabla Tempus Nu3
		user = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
//		user.setNombre("JOSE");
//		user.setUsuario("234");
//		List<OrganizacionesModel> l = new ArrayList<>();
//		user.setOrganizaciones(l);
//		List<Departamento> d = new ArrayList<>();
//		user.setDepartamentos(d);

		if (user.getUsuario() == null) {
			// Busca en tabla Tempus Accesos
			user = usuariosPort.encontrarUsuarioTempusAccesos(solicitud.getUsuario());
		}

		// Sumar total comprobado
		BigDecimal totalComprobado = cfdiPort.totalComprobado(Integer.parseInt(solicitud.getNumeroSolicitud())) != null
				? cfdiPort.totalComprobado(Integer.parseInt(solicitud.getNumeroSolicitud()))
				: BigDecimal.ZERO;
		// solicitud.setTotalComprobado(BigDecimal.ZERO);
		solicitud.setTotalComprobado(totalComprobado);

		byte[] pdfBytes = null;
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Document document = new Document(PageSize.LETTER);
		document.setMargins(12, 12, 30, 20);
		document.setMarginMirroring(true);

		if (local) {
			String nombreArchivo = rutaPdfLocal;
			PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
		} else {
			PdfWriter.getInstance(document, byteStream);
		}
		document.open();
		generaDocumentoGeneral(document, solicitud, user);

		document.close();

		pdfBytes = byteStream.toByteArray();
		return pdfBytes;
	}

	public void generaDocumentoGeneral(Document document, Solicitud solicitud, Usuario usuario)
			throws FileNotFoundException, DocumentException {
		crearTablaLogo(document, solicitud.getNumeroSolicitud());
		espacio(document, 25f);
		cabeceraSolicitud(document, solicitud, usuario);
		espacio(document, 15f);
		observaciones(document,
				solicitud.getObservaciones() == null ? "Sin observaciones" : solicitud.getObservaciones());
		espacio(document, 25f);
		List<Comprobante> comprobantes = solicitud.getComprobantes();
		if (comprobantes != null) {
			log.info("Tamaño comprobantes:" + comprobantes.size());
			if (!comprobantes.isEmpty()) {
				titulo(document, "Comprobantes");
				espacio(document, 5f);
				datosDetalle(document, comprobantes);
			}
		} else {
			titulo(document, "No se han cargado comprobantes aun");
			espacio(document, 5f);
		}
		espacio(document, 20f);

		SolicitudViaticosEntity s = new SolicitudViaticosEntity();
		s.setId(Integer.parseInt(solicitud.getNumeroSolicitud()));
		List<EventoViaticoEntity> el = eventoPort.obtenerBitacoraSolicitud(s);
		titulo(document, "Eventos de viáticos");
		espacio(document, 5f);
		datosEventosViatico(document, el);
		espacio(document, 100f);
		firmas(document, solicitud);
	}

	public static void crearTablaLogo(Document document, String numeroSolicitud) throws DocumentException {

		log.info("generando sección de logo de viatico");

		PdfPTable tabla = new PdfPTable(2);
		tabla.setWidthPercentage(100);
		tabla.setWidths(new int[] { 7, 12 });
		PdfPCell cell = null;
		try {

			// System.out.println("ruta:"+rutaLogo);
			// Image img = Image.getInstance("C:\\assets\\logo\\SLAPI.png");
			Image img = Image.getInstance("C:\\assets\\logo\\VACIO.png");
			img.setWidthPercentage(50);

			cell = new PdfPCell(img, true);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(0);
			cell.setFixedHeight(50f);
			tabla.addCell(cell);

			tabla.addCell(PdfUtilidad.cell("Reporte de solicitud núm. " + numeroSolicitud, negritaTitulo, 0,
					"izquierda", ""));

			document.add(tabla);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public PdfPTable cabeceraSolicitud(Document document, Solicitud solicitud, Usuario usuario)
			throws FileNotFoundException, DocumentException {

		log.info("generando sección de cabecera de viatico");

		BigDecimal totalT = new BigDecimal("0.00");
		BigDecimal noAplicaT = new BigDecimal("0.00");
		String apNoAplica = "";

		List<Comprobante> comprobantes = solicitud.getComprobantes();

		if (comprobantes != null) {
			if (!comprobantes.isEmpty()) {
				for (Comprobante c : comprobantes) {
					if (c.isAprobacionNoAplica()) {
						apNoAplica = "X";
					} else {
						apNoAplica = "";
					}
					Cfdi cfdi = c.getCfdi();
					if (cfdi != null) {
						//totalT = totalT.add(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO);
						
						System.out.println("apNoAplica:"+apNoAplica+"/Monto aprobado:"+c.getMontoAprobado()+ " BDC:"+BigDecimal.ZERO + "/cfdi total:"+cfdi.getTotal());
						if ("".equals(apNoAplica)) {
							noAplicaT = noAplicaT.add(c.getNoAplica() != null ? c.getNoAplica() : BigDecimal.ZERO);
							totalT = totalT.add(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO);
							System.out.println("Se sumo total cfdi:"+totalT);
						} else {
							noAplicaT = noAplicaT.add(new BigDecimal(0.0));
							if(c.getMontoAprobado() !=null) {
								
								if(c.getMontoAprobado().equals(new BigDecimal(0.0))) {
									System.out.println("monto comprobado es 0 suma el total");
									totalT = totalT.add(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO);
								}else {
									System.out.println("se suma el monto comprobado");
									totalT = totalT.add(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO);
								}
							}else {
								System.out.println("Se suma total el monto comp es null");
								totalT = totalT.add(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO);
							}
							System.out.println("Se sumo totalT:"+totalT);
						}
					} else {
						//totalT = totalT.add(c.getTotal());
						
						System.out.println("apNoAplica:"+apNoAplica+"/Monto aprobado:"+c.getMontoAprobado()+ " BDC:"+BigDecimal.ZERO + "/comp total:"+c.getTotal());
						if ("".equals(apNoAplica)) {
							noAplicaT = noAplicaT.add(c.getNoAplica() != null ? c.getNoAplica() : BigDecimal.ZERO);
							totalT = totalT.add(c.getTotal());
							System.out.println("Se sumo total comp:"+totalT);
						} else {
							noAplicaT = noAplicaT.add(BigDecimal.ZERO);
							if(c.getMontoAprobado() !=null) {
								
								if(c.getMontoAprobado().equals(BigDecimal.ZERO)) {
									System.out.println("monto comprobado es 0 suma el total");
									totalT = totalT.add(c.getTotal() != null ? c.getTotal() : BigDecimal.ZERO);
								}else {
									System.out.println("se suma el monto comprobado");
									totalT = totalT.add(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO);
								}
							}else {
								System.out.println("Se suma total el monto comp es null");
								totalT = totalT.add(c.getTotal());
							}
						}
						System.out.println("Se sumo totalT:"+totalT);
					}
				}
			}
		}

		totalT = totalT.subtract(noAplicaT);
		setTotalMenosNoAplica(totalT);

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 2, 5, 2, 2 });

		table.addCell(PdfUtilidad.cell("Empleado", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(usuario.getNombre(), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Fecha", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(new Date()), normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Empresa", negrita, 0, "izquierda", ""));
		System.out.println("orgs:" + usuario.getOrganizaciones().size());
		if (usuario.getOrganizaciones().isEmpty()) {
			table.addCell(PdfUtilidad.cell("N/A", normal, 0, "izquierda", ""));
		} else {
			table.addCell(PdfUtilidad.cell(usuario.getOrganizaciones().get(0).getNombre(), normal, 0, "izquierda", ""));
		}

		table.addCell(PdfUtilidad.cell("Hora", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(UtilidadesAdapter.horaActual(new Date()), normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Centro de costo", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(solicitud.getCecoDesc(), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Concepto Timbrado", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(solicitud.getConcepto(), normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Departamento", negrita, 0, "izquierda", ""));
		if (usuario.getDepartamentos().isEmpty()) {
			table.addCell(PdfUtilidad.cell("N/A", normal, 0, "izquierda", ""));
		} else {
			table.addCell(
					PdfUtilidad.cell(usuario.getDepartamentos().get(0).getDescripcion(), normal, 0, "izquierda", ""));
		}
		table.addCell(PdfUtilidad.cell("Monto solicitado", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(solicitud.getTotalAnticipo(), normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Motivo", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(solicitud.getMotivo(), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Monto comprobado", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(totalT, normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Estatus actual", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(solicitud.getEstatusDescripcion(), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("Periodo", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(solicitud.getFechaInicio()) + " a "
				+ UtilidadesAdapter.formatearFecha(solicitud.getFechaFin()), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));

		document.add(table);

		return table;
	}

	public static void observaciones(Document document, String observaciones)
			throws FileNotFoundException, DocumentException {

		log.info("generando sección de observaciones de viatico");

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 20 });

		table.addCell(PdfUtilidad.cell("Observaciones", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(observaciones == null ? "" : observaciones, normal, 0, "izquierda", ""));

		document.add(table);
	}

	public PdfPTable datosDetalle(Document document, List<Comprobante> comprobantes)
			throws FileNotFoundException, DocumentException {

		log.info("generando sección de datos detalle de viatico");
		String apNoAplica = "";

		PdfPTable table = new PdfPTable(15);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1 });

		table.addCell(PdfUtilidad.cell("Fecha", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("RFC", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("N\u00fam. factura", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("IVA", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("ISR", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("IEPS", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Subtotal", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Total", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Propina", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Observaciones", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Rubro", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("No aplica", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("No descontar", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("No aplica descontar", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Monto aprobado", negritaTitulos, 1, "izquierda", "gris"));

		BigDecimal ivaT = new BigDecimal("0.00");
		BigDecimal isrT = new BigDecimal("0.00");
		BigDecimal iepsT = new BigDecimal("0.00");
		BigDecimal subtotalT = new BigDecimal("0.00");
		BigDecimal totalT = new BigDecimal("0.00");
		BigDecimal noAplicaT = new BigDecimal("0.00");
		BigDecimal noAplicaTD = new BigDecimal("0.00");
		BigDecimal montoAT = new BigDecimal("0.00");
		BigDecimal propinaT = new BigDecimal("0.00");
		BigDecimal noAplicaCalc = BigDecimal.ZERO;
		BigDecimal montoAprobado = BigDecimal.ZERO;
		
		if (comprobantes != null) {
			if (!comprobantes.isEmpty()) {
				for (Comprobante c : comprobantes) {
					if (c.isAprobacionNoAplica()) {
						apNoAplica = "X";
					} else {
						apNoAplica = "";
					}
					Cfdi cfdi = c.getCfdi();
					if (cfdi != null) {
						table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(c.getFecha()), normalTabla, 1,
								"izquierda", ""));
						table.addCell(PdfUtilidad.cell(cfdi.getRfcEmisor(), normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell(c.getNumeroFactura() != null ? c.getNumeroFactura() : "",
								normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell(
								cfdi.getIvaTrasladado() != null ? cfdi.getIvaTrasladado() : BigDecimal.ZERO,
								normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(
								cfdi.getIsrRetenido() != null ? cfdi.getIsrRetenido() : BigDecimal.ZERO, normalTabla, 1,
								"derecha", ""));
						table.addCell(PdfUtilidad.cell(
								cfdi.getIepsTrasladado() != null ? cfdi.getIepsTrasladado() : BigDecimal.ZERO,
								normalTabla, 1, "derecha", ""));
						table.addCell(
								PdfUtilidad.cell(cfdi.getSubtotal() != null ? cfdi.getSubtotal() : BigDecimal.ZERO,
										normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO,
								normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getPropina() != null ? c.getPropina() : BigDecimal.ZERO,
								normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(!c.getObservaciones().equals("") ? c.getObservaciones() : "",
								normalTabla, 1, "izquierda", ""));
						table.addCell(
								PdfUtilidad.cell(c.getSubCuenta() != null ? c.getSubCuenta().getDescripcion() : "",
										normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell(c.getNoAplica(), normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(apNoAplica, normalTabla, 1, "izquierda", ""));
						
						if (c.getMontoAprobado() != null) {
							montoAprobado = c.getMontoAprobado();
						}else {
							montoAprobado = BigDecimal.ZERO;
						}
						if (montoAprobado.compareTo(BigDecimal.ZERO) == 0) {
							noAplicaCalc = BigDecimal.ZERO;
						} else {
							noAplicaCalc = c.getTotal().subtract(montoAprobado);
						}
						
						table.addCell(
								PdfUtilidad.cell(noAplicaCalc,normalTabla, 1, "derecha", ""));
						table.addCell(
								PdfUtilidad.cell(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO,normalTabla, 1, "derecha", ""));

						// Sumando totales
						ivaT = ivaT.add(cfdi.getIvaTrasladado() != null ? cfdi.getIvaTrasladado() : BigDecimal.ZERO);
						isrT = isrT.add(cfdi.getIsrRetenido() != null ? cfdi.getIsrRetenido() : BigDecimal.ZERO);
						iepsT = iepsT.add(cfdi.getIepsRetenido() != null ? cfdi.getIepsRetenido() : BigDecimal.ZERO);
						subtotalT = subtotalT.add(cfdi.getSubtotal() != null ? cfdi.getSubtotal() : BigDecimal.ZERO);
						totalT = totalT.add(cfdi.getTotal() != null ? cfdi.getTotal() : BigDecimal.ZERO);
						propinaT = propinaT.add(c.getPropina() != null ? c.getPropina() : BigDecimal.ZERO);
						if ("".equals(apNoAplica)) {
							noAplicaT = noAplicaT.add(c.getNoAplica() != null ? c.getNoAplica() : BigDecimal.ZERO);
						} else {
							noAplicaT = noAplicaT.add(BigDecimal.ZERO);
						}
						noAplicaTD = noAplicaTD.add(noAplicaCalc);
						montoAT = montoAT.add(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO);

					} else {

						table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(c.getFecha()), normalTabla, 1,
								"izquierda", ""));
						table.addCell(PdfUtilidad.cell("N/A", normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell("N/A", normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell(c.getIva() != null ? c.getIva() : BigDecimal.ZERO, normalTabla,
								1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getIsr() != null ? c.getIsr() : BigDecimal.ZERO, normalTabla,
								1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getIeps() != null ? c.getIeps() : BigDecimal.ZERO, normalTabla,
								1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getSubTotal(), normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getTotal(), normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(c.getPropina() != null ? c.getPropina() : BigDecimal.ZERO,
								normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(!c.getObservaciones().equals("") ? c.getObservaciones() : "",
								normalTabla, 1, "izquierda", ""));
						table.addCell(
								PdfUtilidad.cell(c.getSubCuenta() != null ? c.getSubCuenta().getDescripcion() : "",
										normalTabla, 1, "izquierda", ""));
						table.addCell(PdfUtilidad.cell(c.getNoAplica(), normalTabla, 1, "derecha", ""));
						table.addCell(PdfUtilidad.cell(apNoAplica, normalTabla, 1, "izquierda", ""));

						if (c.getMontoAprobado() != null) {
							montoAprobado = c.getMontoAprobado();
						}else {
							montoAprobado = BigDecimal.ZERO;
						}
						if (montoAprobado.compareTo(BigDecimal.ZERO) == 0) {
							noAplicaCalc = BigDecimal.ZERO;
						} else {
							noAplicaCalc = c.getTotal().subtract(montoAprobado);
						}
						
						table.addCell(
								PdfUtilidad.cell(noAplicaCalc,normalTabla, 1, "derecha", ""));
						
						table.addCell(
								PdfUtilidad.cell(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO,
										normalTabla, 1, "derecha", ""));

						// Sumando totales
						ivaT = ivaT.add(BigDecimal.ZERO);
						isrT = isrT.add(BigDecimal.ZERO);
						iepsT = iepsT.add(BigDecimal.ZERO);
						subtotalT = subtotalT.add(c.getSubTotal());
						totalT = totalT.add(c.getTotal());
						propinaT = propinaT.add(c.getPropina() != null ? c.getPropina() : BigDecimal.ZERO);
						if ("".equals(apNoAplica)) {
							noAplicaT = noAplicaT.add(c.getNoAplica() != null ? c.getNoAplica() : BigDecimal.ZERO);
						} else {
							noAplicaT = noAplicaT.add(BigDecimal.ZERO);
						}
						noAplicaTD = noAplicaTD.add(noAplicaCalc);
						montoAT = montoAT.add(c.getMontoAprobado() != null ? c.getMontoAprobado() : BigDecimal.ZERO);

					}
				}

				log.info("Totales");
				// totalT = totalT.subtract(noAplicaT);
				setTotalMenosNoAplica(totalT);
				table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
				table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
				table.addCell(PdfUtilidad.cell("Totales", negritaTabla, 0, "derecha", ""));
				table.addCell(PdfUtilidad.cell(ivaT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(isrT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(iepsT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(subtotalT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(totalT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(propinaT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
				table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
				table.addCell(PdfUtilidad.cell(noAplicaT, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
				table.addCell(PdfUtilidad.cell(noAplicaTD, normalTabla, 1, "derecha", ""));
				table.addCell(PdfUtilidad.cell(montoAT, normalTabla, 1, "derecha", ""));

				document.add(table);
			}
		}

		return table;
	}

	public static PdfPTable datosEventosViatico(Document document, List<EventoViaticoEntity> eventos)
			throws FileNotFoundException, DocumentException {

		log.info("generando sección de eventos de viatico");

		PdfPTable table = new PdfPTable(13);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 2, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);

		table.addCell(PdfUtilidad.cell("Fecha y Hora", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Evento", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Texto", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Usuario", negritaTitulos, 1, "izquierda", "gris"));

		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
		table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));

		if (eventos != null) {
			if (!eventos.isEmpty()) {
				for (EventoViaticoEntity e : eventos) {

					table.addCell(PdfUtilidad.cell(
							e.getFecha() == null ? "" : UtilidadesAdapter.formatearFechaConHora(e.getFecha()),
							normalTabla, 1, "izquierda", ""));
					table.addCell(PdfUtilidad.cell(e.getEvento() == null ? "" : e.getEvento(), normalTabla, 1,
							"izquierda", ""));
					table.addCell(PdfUtilidad.cell(e.getTexto() == null ? "" : e.getTexto(), normalTabla, 1,
							"izquierda", ""));
					table.addCell(PdfUtilidad.cell(e.getUsuario() == null ? "" : e.getUsuario(), normalTabla, 1,
							"izquierda", ""));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));
					table.addCell(PdfUtilidad.cell("", negritaTitulos, 0, "izquierda", "blanco"));

				}
				document.add(table);

			}
		}

		return table;
	}

	public static PdfPTable firmas(Document document, Solicitud solicitud)
			throws FileNotFoundException, DocumentException {

		log.info("generando sección de firmas de viatico");

		PdfPTable table = new PdfPTable(11);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 2, 2, 2, 1, 1, 1, 2, 2, 2, 1 });

		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 1, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));

		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Elabora", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Solicita", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negritaTabla, 0, "izquierda", ""));

		document.add(table);

		return table;
	}

	public static void titulo(Document document, String titulo) throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 1, 1, 1, });
		table.addCell(PdfUtilidad.cell(titulo, negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		document.add(table);
	}

	public static void espacio(Document document, float altura) throws FileNotFoundException, DocumentException {

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 20 });

		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(altura);
		cell.setBorder(0);
		table.addCell(cell);

		document.add(table);
	}

	public BigDecimal getTotalMenosNoAplica() {
		return TotalMenosNoAplica;
	}

	public void setTotalMenosNoAplica(BigDecimal totalMenosNoAplica) {
		TotalMenosNoAplica = totalMenosNoAplica;
	}

}
