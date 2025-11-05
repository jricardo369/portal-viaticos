package com.viaticos.adapter.out.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.viaticos.adapter.out.file.EventosAdapter;
import com.viaticos.application.ViaticosDeUsuarioService;
//import com.viaticos.adapter.out.sql.wrappers.SolicitudRM;
// HGG	import com.viaticos.adapter.out.sql.wrappers.SolicitudRM;
import com.viaticos.application.port.out.AutorizacionesPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.application.port.out.jpa.mysql.ComprobanteViaticoJPA;
import com.viaticos.application.port.out.jpa.mysql.EstatusSolicitudJPA;
import com.viaticos.application.port.out.jpa.mysql.EventosDeViaticosJPA;
import com.viaticos.application.port.out.jpa.mysql.SolicitudDeUsuariosJPA;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;
import com.viaticos.domain.sql.nu3.OrganizacionesModel;

@Service
public class SolicitudDeViaticosRepository implements SolicitudesDeUsuarioPort, AutorizacionesPort {

	@Autowired
	JdbcTemplate template;

	@SuppressWarnings("unused")
	private String sentenciaSql;

	@Autowired
	UsuariosPort usPort;

	@Autowired
	EventosAdapter ea;

	@Autowired
	private SolicitudDeUsuariosJPA solicitudJPA;

	@Autowired
	private EstatusSolicitudJPA estatusJPA;

	@Autowired
	private EventosDeViaticosJPA eventoJpa;

	@Autowired
	private ComprobanteViaticoJPA comprobanteJpa;

	@Autowired
	private ViaticosDeUsuarioService viaticosService;

	@Override
	public List<Solicitud> encontrarSolicitudesDeEmpladoPorEstatus(String empleado, String estatus) {
		// Consulta para obtener solicitudes de usuario
		List<Solicitud> l = new ArrayList<Solicitud>();

		// Consulta
		sentenciaSql = "SELECT numero_solicitud,motivo,fecha_inicio,fecha_fin,"
				+ "anticipo,empleado,estatus,empresa,ceco,concepto,observaciones " + "FROM solicitud_de_viaticos";
		// l = template.query(sentenciaSql, new SolicitudRM());
		for (Solicitud solicitud : l) {
			solicitud.setUsuarioObj(usPort.encontrarUsuarioPorId(solicitud.getUsuario()));
		}
		System.out.println("lista:" + l.size());

		// }
		return l;
	}

	@Override
	public String crearSolicitudDeUsuario(Solicitud solicitud) {
		// Insertar solicitud en base de datos
		return null;
	}

	@Override
	public void actualizarAutorizacionCambioEstatus(String numeroSolicitud, String estatus) {
		// Actualizar en base de datos
	}

	@Override
	public void actualizarAutorizacionRechazar(String numeroSolicitud, String estatus, String motivo) {
		// Actualizar en base de datos
	}

	@Override
	public List<Solicitud> obtenerSolicitudesPendientesDeAutorizacion(String usuario, String estatus) {

		// Consultamos usuario para saber empresas
		Usuario user = new Usuario();
		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();
		List<SolicitudViaticosEntity> solicitudesEntity = new ArrayList<>();
		List<String> empleadosStr = new ArrayList<String>();
		List<Usuario> empleadoInfo = new ArrayList<Usuario>();

		List<Solicitud> solicitudM = new ArrayList<Solicitud>();
		List<String> organizaciones = new ArrayList<String>();

		estatusEntity = estatusJPA.findById(Integer.parseInt(estatus));
		user = usPort.encontrarUsuarioTempusAccesos(usuario);

		for (OrganizacionesModel org : user.getOrganizaciones()) {
			organizaciones.add(org.getId().replace(" ", ""));
		}

		solicitudesEntity = solicitudJPA.findByEmpresaAndEstatus(organizaciones, estatusEntity);

		for (SolicitudViaticosEntity s : solicitudesEntity) {

			empleadosStr.add(s.getUsuario());

		}

		empleadoInfo = usPort.encontrarEmpleados(empleadosStr);

		for (SolicitudViaticosEntity s : solicitudesEntity) {

			for (Usuario u : empleadoInfo) {
				if (u.getUsuario().contains(s.getUsuario())) {
					user = u;
					break;
				}
			}

			solicitudM.add(this.viaticosService.solicitud(s, user));
		}

		return solicitudM;
	}

	private Solicitud entityToModelSolicitud(SolicitudViaticosEntity en) {

		Solicitud solicitud = new Solicitud();

		solicitud.setNumeroSolicitud(String.valueOf(en.getId()));
		solicitud.setMotivo(en.getMotivo());
		solicitud.setFechaInicio(en.getFechaInicio());
		solicitud.setFechaFin(en.getFechaFin());
		solicitud.setFechaCreacion(en.getFechaCreacion());
		solicitud.setTotalAnticipo(en.getAnticipo());
		solicitud.setUsuario(en.getUsuario());
		solicitud.setEmpresa(en.getEmpresa());
		solicitud.setCeco(en.getCeco());
		solicitud.setObservaciones(en.getObservaciones());
		solicitud.setConcepto(en.getConcepto());
		solicitud.setEstatus(String.valueOf(en.getEstatus().getId()));
		solicitud.setEstatusDescripcion(en.getEstatus().getDescripcion());
		solicitud.setNombreCompletoUsuario(en.getNombreCompletoUsuario());
		solicitud.setCecoDesc(en.getCecoDescr());
		solicitud.setEmpresaDescr(en.getEmpresaDescr());

		return solicitud;
	}

	@Override
	public Solicitud obtenerSolicitud(String numeroSolicitud) {
		// Consulta para obtener solicitudes de usuario
		List<Solicitud> l = new ArrayList<Solicitud>();
		sentenciaSql = "SELECT numero_solicitud,motivo,fecha_inicio,fecha_fin,"
				+ "anticipo,empleado,estatus,empresa,ceco,concepto,observaciones "
				+ "FROM solicitud_de_viaticos WHERE numero_solicitud = ?";
		// l = template.query(sentenciaSql, new SolicitudRM(), numeroSolicitud);
		for (Solicitud solicitud : l) {
			solicitud.setUsuarioObj(usPort.encontrarUsuarioPorId(solicitud.getUsuario()));
		}
		System.out.println("lista:" + l.size());
		if (!l.isEmpty()) {
			return l.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<Solicitud> encontrarSolicitudesDePorEstatus(String estatus) {
		// Consulta para obtener solicitudes de usuario
		List<Solicitud> l = new ArrayList<Solicitud>();
		for (Solicitud solicitud : l) {
			solicitud.setUsuarioObj(usPort.encontrarUsuarioPorId(solicitud.getUsuario()));
		}
		return l;
	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesDeEmpladoPorEstatusJPA(String empleado,
			List<Integer> estatus) {

		List<SolicitudViaticosEntity> sol;
		List<EstatusSolicitudEntity> lstEstatus = new ArrayList<EstatusSolicitudEntity>();
		lstEstatus = estatusJPA.buscaEstatusList(estatus);
		// estSol = estatusJPA.findById(Integer.parseInt(estatus));
		sol = solicitudJPA.encuentraUsuarioEstatuslista(empleado, lstEstatus);
		return sol;

	}

	@Override
	public SolicitudViaticosEntity obtenerSolicitudJPA(int numeroSolicitud) {
		return solicitudJPA.findBynumeroSolicitud(numeroSolicitud);
	}

	@Override
	public SolicitudViaticosEntity crearSolicitud(String solicitante, SolicitudViaticosEntity solicitud) {
		return solicitudJPA.save(solicitud);
	}

	@Override
	public void crearEventoSolicitud(EventoViaticoEntity eventoViatico) {
		eventoJpa.save(eventoViatico);
	}

	@Override
	public void eliminarSolicitud(SolicitudViaticosEntity solEntity) {

		solicitudJPA.delete(solEntity);

	}

	@Override
	public void editarSolicitud(SolicitudViaticosEntity solicitud) {
		solicitudJPA.save(solicitud);

	}

	@Override
	public void enviaPeticionAceptacion(int solicitud, EstatusSolicitudEntity estatus) {

		solicitudJPA.actualizaEstatusPeticion(solicitud, estatus);

	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEstatus(List<EstatusSolicitudEntity> estatus) {

		return solicitudJPA.findByEstatus(estatus);
	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEvento(String evento) {

		return solicitudJPA.findByEvento(evento);
	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYEstatus(String evento, String estatus) {

		return solicitudJPA.findByEventoYEstatus(evento, estatus);
	}

	@Override
	public SolicitudViaticosEntity obtenerEventoPorSolicitud(String evento, int sol) {
		SolicitudViaticosEntity se = solicitudJPA.obtenerEventoPorSolicitud(evento, sol);
		return se;
	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistema(String evento, String sistema) {

		return solicitudJPA.findByEventoPorSistema(evento, sistema);
	}

	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistemaYEstatus(String evento, String sistema,String estatus) {

		return solicitudJPA.findByEventoPorSistemaYEstatus(evento, sistema, estatus);
	}
	
	@Override
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistemaYEstatusNotIn(String evento, String sistema,String estatus) {

		return solicitudJPA.findByEventoPorSistemaYEstatusNotIn(evento, sistema, estatus);
	}

	
	@Override
	public void enviaPeticionComprobante(int idComprobante, String estatusDescripcion) {

		comprobanteJpa.actualizaEstatusEnviaPeticion(idComprobante, estatusDescripcion);

	}

	@Override
	public List<Solicitud> obtenerSolicitudesReporte(String estatus, String empresas, String fechaInicio,
			String fechaFin, int numeroSolicitud) {

		List<String> empresaList = Arrays.asList(empresas.split(",", -1));
		List<String> estatusList = Arrays.asList(estatus.split(",", -1));

		List<SolicitudViaticosEntity> solicitudesEntity = new ArrayList<>();

		List<Solicitud> sols = new ArrayList<Solicitud>();

		if (numeroSolicitud != 0) {
			solicitudesEntity = solicitudJPA.ObtenerSoloSolicitudPorNumeroSol(numeroSolicitud);
		} else {
			solicitudesEntity = solicitudJPA.findForReporte(estatusList, fechaInicio, fechaFin, empresaList);
		}

		System.out.println("Se obtuvieron solicitudes");
		for (SolicitudViaticosEntity s : solicitudesEntity) {

			sols.add(entityToModelSolicitud(s));

		}
		return sols;
	}

	@Override
	public List<Solicitud> obtenerSolicitudesReporteDirector(String estatus, String empresas, String fechaInicio,
			String fechaFin) {

		List<String> empresaList = Arrays.asList(empresas.split(",", -1));
		List<String> estatusList = Arrays.asList(estatus.split(",", -1));

		List<SolicitudViaticosEntity> solicitudesEntity = new ArrayList<>();

		List<Solicitud> sols = new ArrayList<Solicitud>();

		solicitudesEntity = solicitudJPA.findForReporteDirector(estatusList, fechaInicio, fechaFin, empresaList);

		for (SolicitudViaticosEntity s : solicitudesEntity) {

			sols.add(entityToModelSolicitud(s));

		}
		return sols;
	}

	@Override
	public List<SolicitudViaticosEntity> obtenerSolicitudesPorEmpresasYEstatus(List<Integer> estatus,
			List<String> empresas, String rol) {

		List<EstatusSolicitudEntity> estatusEnt = new ArrayList<EstatusSolicitudEntity>();
		List<SolicitudViaticosEntity> solicituEnt = new ArrayList<SolicitudViaticosEntity>();

		estatusEnt = estatusJPA.buscaEstatusList(estatus);

		if (rol.equalsIgnoreCase("director"))
			solicituEnt = solicitudJPA.obtenerPorEmpresasYEstatusYDirector(empresas, estatusEnt);
		else
			solicituEnt = solicitudJPA.obtenerPorEmpresasYEstatus(empresas, estatusEnt);

		return solicituEnt;
	}
	
	@Override
	public List<SolicitudViaticosEntity> obtenerSolicitudesPorEmpresasYEstatusDirector(List<Integer> estatus,
			List<String> empresas) {

		List<EstatusSolicitudEntity> estatusEnt = new ArrayList<EstatusSolicitudEntity>();
		List<SolicitudViaticosEntity> solicituEnt = new ArrayList<SolicitudViaticosEntity>();

		estatusEnt = estatusJPA.buscaEstatusList(estatus);
		solicituEnt = solicitudJPA.obtenerPorEmpresasYEstatusYDirector(empresas, estatusEnt);

		return solicituEnt;
	}

	@Override
	public List<Solicitud> obtenerPorEventoYFechas(String evento, String fechaInicio, String fechaFin) {
		List<SolicitudViaticosEntity> solicitudesEntity = new ArrayList<>();
		List<String> eventosNo = ea.eventosNo();
		String a = new String(evento);
		if (eventosNo.contains(a)) {
			String eventoConvertido = ea.conversionNoEvento(evento);
			System.out.println("Evento convertido:" + eventoConvertido);
			System.out.println("Obtener solicitudes por no evento y fechas");

			if (eventoConvertido.equals("Envió de póliza")) {
				solicitudesEntity = solicitudJPA.obtenerPorNoEventoYFechasYEstatus(eventoConvertido, fechaInicio,
						fechaFin, 15);
			} else if (eventoConvertido.equals("Dispersión entrega")) {
				solicitudesEntity = solicitudJPA.obtenerPorNoEventoYFechasYEstatus(eventoConvertido, fechaInicio,
						fechaFin, 3);
			} else if (eventoConvertido.equals("Comprobación")) {
				solicitudesEntity = solicitudJPA.obtenerPorNoEventoYFechasYEstatus(eventoConvertido, fechaInicio,
						fechaFin, 14);
			} else {
				solicitudesEntity = solicitudJPA.obtenerPorNoEventoYFechas(eventoConvertido, fechaInicio, fechaFin);
			}

		}
		List<String> eventos = ea.eventos();
		if (eventos.contains(evento)) {
			System.out.println("Obtener solicitudes por evento y fechas");
			solicitudesEntity = solicitudJPA.obtenerPorEventoYFechas(evento, fechaInicio, fechaFin);
		}

		List<Solicitud> sols = new ArrayList<Solicitud>();
		System.out.println("Se obtuvieron las solicitudes");
		for (SolicitudViaticosEntity s : solicitudesEntity) {
			sols.add(entityToModelSolicitud(s));
		}

		return sols;
	}

	@Override
	public List<Solicitud> obtenerSoloSolicitudPorNumSolicitud(int numeroSolicitud) {
		List<SolicitudViaticosEntity> solicitudesEntity = new ArrayList<>();
		solicitudesEntity = solicitudJPA.ObtenerSoloSolicitudPorNumeroSol(numeroSolicitud);
		List<Solicitud> sols = new ArrayList<Solicitud>();
		System.out.println("Se obtuvieron las solicitudes");
		for (SolicitudViaticosEntity s : solicitudesEntity) {

			sols.add(entityToModelSolicitud(s));

		}
		return sols;
	}

	@Override
	public List<SolicitudViaticosEntity> obtenerSolicitudesFueraRango(int dias) {
		return solicitudJPA.obtenerSolicitudesFueraRango(dias);

	}


}
