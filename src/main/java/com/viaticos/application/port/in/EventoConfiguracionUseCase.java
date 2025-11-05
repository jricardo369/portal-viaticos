package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.EventoConfiguracionEntity;

public interface EventoConfiguracionUseCase {
	
	public EventoConfiguracionEntity obtenerEvento(String evento);
	public List<EventoConfiguracionEntity> obtenerevento();
	public void insertarEvento(EventoConfiguracionEntity evento);

}
