package com.viaticos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.AutorizacionesUseCase;
import com.viaticos.application.port.out.AutorizacionesPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.EnviarCorreoPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;

@Service
public class AutorizacionesService implements AutorizacionesUseCase {

	@Autowired
	private AutorizacionesPort autPort;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EnviarCorreoPort envioCorreoPort;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Autowired
	private EventoDeViaticoPort eventoPort;

	@Override
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatus(String usuario, String estatus) {

		return autPort.obtenerSolicitudesPendientesDeAutorizacion(usuario, estatus);
	}

	@Override
	public void autorizarSolicitud(String usuario, String numeroSolicitud, String motivo) {

		// Actualizar solicitud
		// autPort.actualizarAutorizacionCambioEstatus(numeroSolicitud, "pendiente
		// autorizar por contador");
		EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
		estatus = estatusPort.obtieneEstatusSolicitud(3);
		if (estatus == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");

		solUsPort.enviaPeticionAceptacion(Integer.valueOf(numeroSolicitud), estatus);

		// Buscar usuario, en este caso es el aprobador
		// Usuario usAprobador = usuariosPort.encontrarUsuarioPorId(usuario);
		Usuario usAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (usAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador, " + usuario);

		// Buscar solicitud
		// Solicitud solicitud = solUsPort.obtenerSolicitud(numeroSolicitud);
		SolicitudViaticosEntity solicitud = solUsPort.obtenerSolicitudJPA(Integer.valueOf(numeroSolicitud));
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		// Obtener datos de usuario de solicitud
		Usuario usSolicitud = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
		if (usSolicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario solicitante");

		// Alta en bitacora
		motivo = motivo == null ? "" : motivo;
		eventoPort.ingresarEventoDeSolicitud("Solicitud aprobada", motivo, solicitud,
				usAprobador.getNombre().toUpperCase());

		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", usAprobador.getNombre());
		params.put("${usuario-solicitante}", usSolicitud.getNombre());
		params.put("${numero-solicitud}", numeroSolicitud);

		// Enviar correo a solicitante
		if (usSolicitud.getCorreoElectronico() != null)
			envioCorreoPort.enviarCorreoAprobacionSolicitud(usSolicitud.getCorreoElectronico(), usSolicitud.getNombre(),
					params);
		else
			eventoPort.ingresarEventoDeSolicitud("Intento de envío de correo", "", solicitud,
					usSolicitud.getNombre().toUpperCase());
		
	}

	@Override
	public void rechazarSolicitud(String usuario, String numeroSolicitud, String motivo) {
	
		EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
		estatus = estatusPort.obtieneEstatusSolicitud(4);
		if (estatus == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");
		solUsPort.enviaPeticionAceptacion(Integer.valueOf(numeroSolicitud), estatus);

		// Buscar usuario, en este caso es el aprobador
		Usuario usAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (usAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador, " + usuario);

		// Buscar solicitud
		SolicitudViaticosEntity solicitud = solUsPort.obtenerSolicitudJPA(Integer.valueOf(numeroSolicitud));
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		if (motivo == null)
			motivo = "";

		solicitud.setObservaciones(motivo);
		solUsPort.editarSolicitud(solicitud);

		// Obtener datos de usuario de solicitud
		Usuario usSolicitud = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
		if (usSolicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario solicitante");

		eventoPort.ingresarEventoDeSolicitud("Solicitud rechazada", motivo, solicitud,
				usAprobador.getNombre().toUpperCase());


		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", usAprobador.getNombre());
		params.put("${usuario-solicitante}", usSolicitud.getNombre());
		params.put("${numero-solicitud}", numeroSolicitud);
		params.put("${motivo}", motivo);

		// Enviar correo a solicitante
		if (usSolicitud.getCorreoElectronico() != null)
			envioCorreoPort.enviarCorreoRechazoSolicitud(usSolicitud.getCorreoElectronico(), usSolicitud.getNombre(),
					params);
		else
			eventoPort.ingresarEventoDeSolicitud("Intento de envío de correo", "", solicitud,
					usSolicitud.getNombre().toUpperCase());
		
	}

}
