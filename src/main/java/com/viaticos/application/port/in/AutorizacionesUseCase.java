package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.Solicitud;

public interface AutorizacionesUseCase {
	
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatus(String usuario,String estatus);
	
	public void autorizarSolicitud(String usuario, String numeroSolicitud, String motivo);
	
	public void rechazarSolicitud(String usuario, String numeroSolicitud,String motivo);

}
