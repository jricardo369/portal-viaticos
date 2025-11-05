package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.Aviso;

public interface AvisoUseCase {
	
	public List<Aviso> obtenerAvisosDeSolicitud(int numeroSolicitud);

}
