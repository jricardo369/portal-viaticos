package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.Solicitud;

public interface AprobacionesDirectorUseCase {

	public List<Solicitud> consultarAprobacionesPendientesDeUsuarioPorEstatus(String usuario,String estatus,String empresas,boolean puedeAprobarSolDir);

	public void autorizarViaje(String usuario, int numeroSolicitud, String motivo,boolean puedeAprobarSolDir);

	public void rechazarViaje(String usuario, int numeroSolicitud, String motivo,boolean puedeAprobarSolDir);

}
