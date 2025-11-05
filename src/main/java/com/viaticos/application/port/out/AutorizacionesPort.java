package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.Solicitud;

public interface AutorizacionesPort {
	
	public List<Solicitud> obtenerSolicitudesPendientesDeAutorizacion(String usuario,String estatus);
	public void actualizarAutorizacionCambioEstatus(String numeroSolicitud, String estatus);
	public void actualizarAutorizacionRechazar(String numeroSolicitud, String estatus, String motivo);

}
