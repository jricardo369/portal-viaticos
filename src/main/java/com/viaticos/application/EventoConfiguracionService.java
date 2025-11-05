package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.EventoConfiguracionUseCase;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.domain.EventoConfiguracionEntity;

@Service
public class EventoConfiguracionService implements EventoConfiguracionUseCase {
	
	@Autowired
	private EventoConfiguracionPort evenconPort;

	@Override
	public EventoConfiguracionEntity obtenerEvento(String evento) {
		return evenconPort.obtenerEventoPorEvento(evento);
	}

	@Override
	public List<EventoConfiguracionEntity> obtenerevento() {
		return evenconPort.obtenerEvento();
	}

	@Override
	public void insertarEvento(EventoConfiguracionEntity evento) {
		evenconPort.insertarEvento(evento);
		
	}

}
