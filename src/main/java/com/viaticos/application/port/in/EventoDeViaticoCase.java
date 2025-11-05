package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.EventoViaticoEntity;

public interface EventoDeViaticoCase {
	
	public List<EventoViaticoEntity> obtenerEventosDeSolicitud(int numeroSolicitud);
	public void crearEventoDeSolicitud(EventoViaticoEntity evento);

}
