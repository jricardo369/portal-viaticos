package com.viaticos.adapter.out.mail;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EnviarCorreoPort;

@Service
public class EmailAdapter implements EnviarCorreoPort {

	@Autowired
	private TemplateMail tpMail;

	@Autowired
	private EnvioCorreoAdapter envioCorreo;

	@Override
	public void enviarCorreoSolicitudAprobacionSolicitante(String email, String usuario, Map<String, Object> params, byte[] attach) {
		String ns = params.get("${numero-solicitud}").toString();
		String template = "";
		try {
			template = tpMail.solveTemplate("email-templates/email-solicitud-aprobacion-solicitante.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreoLst(email, "Petición de aprobación de solicitud de viático núm. " + ns, template, attach);
	}

	@Override
	public void enviarCorreoAprobacionSolicitud(String email, String usuario, Map<String, Object> params) {
		String template = "";
		String ns = params.get("${numero-solicitud}").toString();
		try {
			template = tpMail.solveTemplate("email-templates/email-aprobacion-solicitud.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreo(email, "Aprobación de solicitud de viático núm. " + ns, template);
	}

	@Override
	public void enviarCorreoRechazoSolicitud(String email, String usuario, Map<String, Object> params) {
		String ns = params.get("${numero-solicitud}").toString();
		String template = "";
		try {
			template = tpMail.solveTemplate("email-templates/email-rechazo-solicitud.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreo(email, "Rechazo de solicitud de viático núm. " + ns, template);
	}

	@Override
	public void enviarCorreoAprobacionComprobacion(String email, String usuario, Map<String, Object> params) {
		String ns = params.get("${numero-solicitud}").toString();
		String template = "";
		try {
			template = tpMail.solveTemplate("email-templates/email-aprobacion-comprobacion.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreo(email, "Aprobación de comprobación de viático núm. " + ns, template);
	}

	@Override
	public void enviarCorreoRechazoComprobacion(String email, String usuario, Map<String, Object> params) {
		String template = "";
		String ns = params.get("${numero-solicitud}").toString();
		try {
			template = tpMail.solveTemplate("email-templates/email-rechazo-comprobacion.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreo(email, "Rechazo de comprobación de viático núm. " + ns, template);
	}

	@Override
	public void enviarCorreoAprobacionComprobacionFinal(String email, String usuario, Map<String, Object> params) {
		String ns = params.get("${numero-solicitud}").toString();
		String template = "";
		try {
			template = tpMail.solveTemplate("email-templates/email-aprobacion-comprobacion-final.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreo(email, "Aprobación de comprobación de viático núm. " + ns, template);
	}

	@Override
	public void enviarCorreoSiguienteRol(String email, String usuario, Map<String, Object> params, byte[] attach) {
		String ns = params.get("${numero-solicitud}").toString();
		String template = "";
		try {
			template = tpMail.solveTemplate("email-templates/email-aprobacion-roles.html", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		envioCorreo.enviarCorreoLst(email, "Continuar con el proceso de comprobación " + ns, template, attach);
		
	}



}
