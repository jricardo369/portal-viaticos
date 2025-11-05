package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.Solicitud;

public interface AprobacionesUseCase {

	public List<Solicitud> consultarComprobacionesPendientesDeUsuarioPorEstatus(String usuario,String estatus);

	public void autorizarComprobante(int idComprobante, String estatus, String usuario);

	public void rechazarComprobante(int idComprobante, String motivo, String estatus, String usuario);

	public void autorizarComprobacion(String usuario, int numeroSolicitud, String motivo);

	public void rechazarComprobacion(String usuario, int numeroSolicitud, String motivo);

}
