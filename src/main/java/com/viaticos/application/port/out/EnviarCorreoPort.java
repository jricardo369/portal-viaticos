package com.viaticos.application.port.out;

import java.util.Map;

public interface EnviarCorreoPort {
	
	public void enviarCorreoSolicitudAprobacionSolicitante(String email, String usuario,  Map<String, Object> params, byte[] attach) ;
	public void enviarCorreoAprobacionSolicitud(String email, String usuario,  Map<String, Object> params) ;
	public void enviarCorreoRechazoSolicitud(String email, String usuario,  Map<String, Object> params) ;
	public void enviarCorreoAprobacionComprobacion(String email, String usuario,  Map<String, Object> params) ;
	public void enviarCorreoRechazoComprobacion(String email, String usuario,  Map<String, Object> params) ;
	public void enviarCorreoAprobacionComprobacionFinal(String email, String usuario,  Map<String, Object> params) ;
	public void enviarCorreoSiguienteRol(String email, String usuario,  Map<String, Object> params, byte[] attach);

}
