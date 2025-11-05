package com.viaticos.application;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.DocumentException;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.adapter.out.file.PdfSolicitudViatico;
import com.viaticos.application.port.in.AprobacionesUseCase;
import com.viaticos.application.port.out.AutorizacionesPort;
import com.viaticos.application.port.out.ComprobantesDeViaticosPort;
import com.viaticos.application.port.out.EmpresaAprobacionPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.EnviarCorreoPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.EmpresaAprobacionEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;
import com.viaticos.domain.sql.accesos.UsuarioEntity;
import com.viaticos.domain.sql.nu3.RolModel;

@Service
public class AprobacionesService implements AprobacionesUseCase {

	@Autowired
	private AutorizacionesPort autPort;

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
	private EmpresaAprobacionPort empApPort;

	@Autowired
	private ViaticosDeUsuarioService viaticosUserService;

	@Autowired
	private PdfSolicitudViatico pdfSolicitudViatico;

	@Override
	public List<Solicitud> consultarComprobacionesPendientesDeUsuarioPorEstatus(String usuario, String estatus) {
		List<Solicitud> solicitud = autPort.obtenerSolicitudesPendientesDeAutorizacion(usuario, estatus);
		return solicitud;
	}

	@Override
	public void autorizarComprobacion(String usuario, int numeroSolicitud, String motivo) {

		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();
		// List<UsuarioEntity> users = new ArrayList<UsuarioEntity>();
		List<String> emailUsers = null;

		boolean prestadorEmail = false;
		boolean userDirector = false;
		boolean otrosRoles = false;
		boolean rolContabilidad = false;
		boolean rolGerente = false;
		boolean rolPrestadora = false;
		String rolStr = "";
		String email = null;

		byte[] bytePdf = null;

		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);
		// ApiException(404, "No se encontró solicitud, " + numeroSolicitud);

		// Buscar usuario que está aprobandos
		Usuario usAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (usAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador" + usuario);

		// Obtener datos de usuario de solicitud
		Usuario usSolicitud = usuariosPort.encontrarUsuarioIdJPA(solicitud.getUsuario());
		if (usSolicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario solicitante ");
		
		//Obtener empresas con aprobacion adicional
		List<EmpresaAprobacionEntity> empsAprb = empApPort.obtenerEmpresasAprobacion();
		boolean aprobacionAdicional = UtilidadesAdapter.esAprobacionAdicional(usSolicitud.getOrganizaciones(), empsAprb);
		

		for (RolModel r : usAprobador.getRol()) {

			if (r.getDescripcion().equalsIgnoreCase("director")) {
				userDirector = true;
			}

			if (r.getDescripcion().equalsIgnoreCase("contabilidad")) {
				rolStr = String.join(",", r.getDescripcion());
				otrosRoles = true;
				rolContabilidad = true;
			}

			if (r.getDescripcion().equalsIgnoreCase("gerentes")) {

				otrosRoles = true;
				rolStr = String.join(",", r.getDescripcion());
				rolGerente = true;
			}

			if (r.getDescripcion().equalsIgnoreCase("contador prestadora")) {

				otrosRoles = true;
				rolPrestadora = true;
				rolStr = String.join(",", r.getDescripcion());
			}

		}

		// Cambio de estatus según roles

		// Contabilidad
		if (rolContabilidad) {
			estatusEntity = estatusPort.obtieneEstatusSolicitud(8);
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionContador(true);
				c.setEstatusComprobante(estatusEntity.getDescripcion());

			}
		}

		// Estatus Gerente
		if (rolGerente) {
			estatusEntity = estatusPort.obtieneEstatusSolicitud(11);
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionGerente(true);
				c.setEstatusComprobante(estatusEntity.getDescripcion());

			}
		}

		// Estatus Prestadora
		if (rolPrestadora) {
			if(aprobacionAdicional) {
				estatusEntity = estatusPort.obtieneEstatusSolicitud(16);
			}else {
				estatusEntity = estatusPort.obtieneEstatusSolicitud(14);
			}
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionPrestador(true);
				c.setEstatusComprobante(estatusEntity.getDescripcion());
				prestadorEmail = true;
			}

		}

		// Cambiar la descripcion
		if (userDirector)
			bitacoraPort.ingresarEventoDeSolicitud("Comprobación autorizada por Director", motivo, solicitud,
					usAprobador.getNombre().toUpperCase());

		if (otrosRoles) {

			solicitud.setEstatus(estatusEntity);

			solUsPort.editarSolicitud(solicitud);
			comprobantePort.guardaEstatusComprobacionesDeSolicitud(solicitud.getComprobanteViaticosEntity());

			bitacoraPort.ingresarEventoDeSolicitud("Comprobación autorizada por " + rolStr, motivo, solicitud,
					usAprobador.getNombre().toUpperCase());
		}

		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", usAprobador.getNombre());
		params.put("${usuario-solicitante}", usSolicitud.getNombre());
		params.put("${numero-solicitud}", numeroSolicitud);

		// Enviar correo a solicitante
		if (!userDirector) {

			if (usSolicitud.getCorreoElectronico() != null) {

				if (prestadorEmail) {
					envioCorreoPort.enviarCorreoAprobacionComprobacionFinal(usSolicitud.getCorreoElectronico(),
							usSolicitud.getUsuario(), params);
				} else {
					envioCorreoPort.enviarCorreoAprobacionComprobacion(usSolicitud.getCorreoElectronico(),
							usSolicitud.getUsuario(), params);
				}
			} else {
				bitacoraPort.ingresarEventoDeSolicitud("Intento de envío de correo", "", solicitud,
						usSolicitud.getNombre().toUpperCase());
			}

		}

		// Enviar correo a siguiente aprobador
		// envia correos a gerentes

		try {
			bytePdf = pdfSolicitudViatico
					.generarPdfSolicitud(viaticosUserService.obtenerSolicitudJPA(solicitud.getId()));
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}

		if (rolContabilidad && !rolGerente) {

			List<UsuarioEntity> users = new ArrayList<UsuarioEntity>();
			users = usuariosPort.encontrarUsuarioTempusPorEmpresa(solicitud.getEmpresa(), "gerentes");

			emailUsers = new ArrayList<String>();

			for (UsuarioEntity u : users) {
				emailUsers.add(u.getUsEmail());
			}

			// emailUsers.add("humbertogarciag7@gmaill.com");
			email = StringUtils.join(emailUsers, ",");
			// System.out.println(emailUsers);

			Map<String, Object> paramsGerente = new HashMap<>();
			paramsGerente.put("${usuario-aprobador}", usAprobador.getNombre());
			paramsGerente.put("${numero-solicitud}", solicitud.getId());

			envioCorreoPort.enviarCorreoSiguienteRol(email, "", paramsGerente, bytePdf);

		}

		// Envia correos a prestadores
		if ((!rolContabilidad && rolGerente) || (rolContabilidad && rolGerente)) {
			// users = usuariosPort.encontrarUsuarioTempusPorEmpresa(solicitud.getEmpresa(),
			// "contador prestadora");
			emailUsers = new ArrayList<String>();

			/*
			 * for (UsuarioEntity u : users) { emailUsers.add(u.getUsEmail()); }
			 */

			emailUsers.add("humberto.garcia@agilethought.com");
			email = StringUtils.join(emailUsers, ",");

			Map<String, Object> paramsPrestador = new HashMap<>();
			paramsPrestador.put("${usuario-aprobador}", usAprobador.getNombre());
			paramsPrestador.put("${numero-solicitud}", solicitud.getId());

			envioCorreoPort.enviarCorreoSiguienteRol(email, "", paramsPrestador, bytePdf);

		}

	}

	@Override
	public void rechazarComprobacion(String usuario, int numeroSolicitud, String motivo) {
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		EstatusSolicitudEntity estatusEntity = new EstatusSolicitudEntity();

		boolean rolContabilidad = false;
		boolean rolGerente = false;
		boolean rolPrestadora = false;

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

		for (RolModel r : usAprobador.getRol()) {

			if (r.getDescripcion().equalsIgnoreCase("contabilidad")) {

				rolContabilidad = true;

			}

			if (r.getDescripcion().equalsIgnoreCase("gerentes")) {

				rolGerente = true;
			}

			if (r.getDescripcion().equalsIgnoreCase("contador prestadora")) {

				rolPrestadora = true;
			}

		}

		// Cambio de estatus según roles

		// Contabilidad
		if (rolContabilidad) {
			estatusEntity = estatusPort.obtieneEstatusSolicitud(6);
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionContador(false);
				c.setEstatusComprobante(estatusEntity.getDescripcion());

			}
		}

		// Estatus Gerente
		if (rolGerente) {
			estatusEntity = estatusPort.obtieneEstatusSolicitud(9);
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionGerente(false);
				c.setEstatusComprobante(estatusEntity.getDescripcion());

			}
		}

		// Estatus Prestadora
		if (rolPrestadora) {
			estatusEntity = estatusPort.obtieneEstatusSolicitud(12);
			for (ComprobanteViaticoEntity c : solicitud.getComprobanteViaticosEntity()) {
				c.setAprobacionPrestador(false);
				c.setEstatusComprobante(estatusEntity.getDescripcion());

			}

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

	@Override
	public void autorizarComprobante(int idComprobante, String estatus, String usuario) {

		ComprobanteViaticoEntity comprobante = new ComprobanteViaticoEntity();
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		Usuario userAprobador = new Usuario();

		comprobante = comprobantePort.obtenerDeComprobante(idComprobante);
		if (comprobante == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró comprobante, " + idComprobante);

		solicitud = solUsPort.obtenerSolicitudJPA(comprobante.getNumero_solicitud().getId());
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud");

		userAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (userAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador, " + usuario);

		for (RolModel r : userAprobador.getRol()) {

			if (r.getDescripcion().equalsIgnoreCase("contabilidad"))
				;
			comprobante.setAprobacionContador(true);

			if (r.getDescripcion().equalsIgnoreCase("gerentes"))
				;
			comprobante.setAprobacionGerente(true);

			if (r.getDescripcion().equalsIgnoreCase("contador prestadora"))
				;
			comprobante.setAprobacionPrestador(true);

		}

		comprobante.setEstatusComprobante(estatus);
		comprobantePort.actualizaEstatusComprobante(comprobante);
		;
		bitacoraPort.ingresarEventoDeSolicitud("Comprobante aprobado", "", solicitud,
				userAprobador.getNombre().toUpperCase());

	}

	@Override
	public void rechazarComprobante(int idComprobante, String motivo, String estatus, String usuario) {

		ComprobanteViaticoEntity comprobante = new ComprobanteViaticoEntity();
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		Usuario userAprobador = new Usuario();

		comprobante = comprobantePort.obtenerDeComprobante(idComprobante);
		if (comprobante == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró comprobante, " + idComprobante);

		solicitud = solUsPort.obtenerSolicitudJPA(comprobante.getNumero_solicitud().getId());
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud");

		userAprobador = usuariosPort.encontrarUsuarioTempusAccesos(usuario);
		if (userAprobador == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró usuario aprobador, " + usuario);

		for (RolModel r : userAprobador.getRol()) {

			if (r.getDescripcion().equalsIgnoreCase("contabilidad"))
				;
			comprobante.setAprobacionContador(true);

			if (r.getDescripcion().equalsIgnoreCase("gerentes"))
				;
			comprobante.setAprobacionGerente(true);

			if (r.getDescripcion().equalsIgnoreCase("contador prestadora"))
				;
			comprobante.setAprobacionPrestador(true);

		}

		comprobante.setEstatusComprobante(estatus);
		comprobante.setObservaciones(motivo);
		comprobantePort.actualizaEstatusComprobante(comprobante);
		bitacoraPort.ingresarEventoDeSolicitud("Comprobante rechazado", motivo, solicitud,
				userAprobador.getNombre().toUpperCase());

	}

}
