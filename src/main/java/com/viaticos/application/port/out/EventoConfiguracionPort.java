package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.EventoConfiguracionEntity;

public interface EventoConfiguracionPort {
	
	public EventoConfiguracionEntity obtenerEventoPorId(int id);
	public EventoConfiguracionEntity obtenerEventoPorEvento(String evento);
	public List<EventoConfiguracionEntity> obtenerEvento();
	public void insertarEvento(EventoConfiguracionEntity evento);
	public void insertarEventoCompleto(String evento, String texto,String usuario);

}
