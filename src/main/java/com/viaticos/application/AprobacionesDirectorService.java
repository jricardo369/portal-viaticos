package com.viaticos.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.AprobacionesDirectorUseCase;
import com.viaticos.application.port.out.ComprobantesDeViaticosPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.EnviarCorreoPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;

@Service
public class AprobacionesDirectorService implements AprobacionesDirectorUseCase {

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EventoDeViaticoPort bitacoraPort;

	@Autowired
	private EnviarCorreoPort envioCorreoPort;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Autowired
	private ComprobantesDeViaticosPort comprobantePort;
	
	@Autowired
	private ViaticosDeUsuarioService viaticosService;

	@Override
	public List<Solicitud> consultarAprobacionesPendientesDeUsuarioPorEstatus(String usuario, String estatus,String empresas,boolean puedeAprobarSolDir) {
		
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
		
		if(!puedeAprobarSolDir) {
			user = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
			
			if(user !=  null) {
				
				boolean tieneDirector = user != null && 
	                    user.getRol() != null &&
	                    user.getRol()
	                        .stream()
	                        .anyMatch(r -> "director".equalsIgnoreCase(r.getDescripcion()));
				
				
				if (!tieneDirector) {
						throw new ResponseStatusException(HttpStatus.CONFLICT,"El usuario no tiene permiso de director para visualizar aprobaciones ");	
				}	
			}else {
				throw new ResponseStatusException(HttpStatus.CONFLICT,"El usuario no tiene permiso de director para visualizar aprobaciones ");	
			}
		}

		solicitudEnt = solUsPort.obtenerSolicitudesPorEmpresasYEstatusDirector(estatusInt, empresasStr);

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

			solicitud.add(viaticosService.solicitud(s, user));
		}

		return solicitud;
	}

	@Override
	public void autorizarViaje(String usuario, int numeroSolicitud, String motivo,boolean puedeAprobarSolDir) {

		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();

		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		// Buscar usuario que está aprobandos
		Usuario usAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (usAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador" + usuario);

		// Obtener datos de usuario de solicitud
		Usuario usSolicitud = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
		if (usSolicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario solicitante ");

		
		
		if(!puedeAprobarSolDir) {
			
			if(usAprobador !=  null) {
				
				boolean tieneDirector = usAprobador != null && 
						usAprobador.getRol() != null &&
								usAprobador.getRol()
	                        .stream()
	                        .anyMatch(r -> "director".equalsIgnoreCase(r.getDescripcion()));
				
				
				if (!tieneDirector) {
						throw new ResponseStatusException(HttpStatus.CONFLICT,"El usuario no tiene permiso de director para visualizar aprobaciones ");	
				}	
			}
		}
		
		// Cambio de estatus a cerrada
		estatusEntity = estatusPort.obtieneEstatusSolicitud(14);
		for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
			//c.setAprobacionDirector(true);
			c.setEstatusComprobante(estatusEntity.getDescripcion());
		}

		//Cambiar estatus
		solicitud.setEstatus(estatusEntity);

		// Agregar evento de director
		bitacoraPort.ingresarEventoDeSolicitud("Comprobación autorizada por Director", motivo, solicitud,
				usAprobador.getNombre().toUpperCase());

		
		solUsPort.editarSolicitud(solicitud);
		comprobantePort.guardaEstatusComprobacionesDeSolicitud(solicitud.getComprobanteViaticosEntity());

		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", usAprobador.getNombre());
		params.put("${usuario-solicitante}", usSolicitud.getNombre());
		params.put("${numero-solicitud}", numeroSolicitud);

		// Enviar correo a solicitante
		if (usSolicitud.getCorreoElectronico() != null) {
				envioCorreoPort.enviarCorreoAprobacionComprobacionFinal(usSolicitud.getCorreoElectronico(),
						usSolicitud.getUsuario(), params);
		} else {
			bitacoraPort.ingresarEventoDeSolicitud("Intento de envío de correo", "", solicitud,
					usSolicitud.getNombre().toUpperCase());
		}

	}

	@Override
	public void rechazarViaje(String usuario, int numeroSolicitud, String motivo,boolean puedeAprobarSolDir) {

		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();

		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		// Buscar usuario que está aprobandos
		Usuario usAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (usAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador " + usuario);

		// Obtener datos de usuario de solicitud
		Usuario usSolicitud = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
		if (usSolicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario solicitante");

		
		boolean tieneDirector = usAprobador.getRol().stream().anyMatch(
				rol -> rol.getDescripcion() != null && rol.getDescripcion().toLowerCase().contains("director"));
		
		if (!tieneDirector) {
			if(!puedeAprobarSolDir) {
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"El usuario no tiene permiso de director para rechazar ");	
			}		
		}
		
		// Cambio de estatus
		estatusEntity = estatusPort.obtieneEstatusSolicitud(17);
		for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
			//c.setAprobacionDirector(false);
			c.setEstatusComprobante(estatusEntity.getDescripcion());
		}

		solicitud.setEstatus(estatusEntity);

		solUsPort.editarSolicitud(solicitud);
		comprobantePort.guardaEstatusComprobacionesDeSolicitud(solicitud.getComprobanteViaticosEntity());

		bitacoraPort.ingresarEventoDeSolicitud("Comprobación rechazada", motivo, solicitud,
				usAprobador.getNombre().toUpperCase());

		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", usAprobador.getNombre());
		params.put("${usuario-solicitante}", usSolicitud.getNombre());
		params.put("${numero-solicitud}", numeroSolicitud);
		params.put("${motivo}", motivo);

		// Enviar correo a solicitante
		if (usSolicitud.getCorreoElectronico() != null) {
			envioCorreoPort.enviarCorreoRechazoComprobacion(usSolicitud.getCorreoElectronico(),
					usSolicitud.getUsuario().toUpperCase(), params);
		} else {
			bitacoraPort.ingresarEventoDeSolicitud("Intento de envío de correo", "", solicitud,
					usSolicitud.getNombre().toUpperCase());
		}

	}
	
	
}
