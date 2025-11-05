package com.viaticos.adapter.out.sql;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.application.port.out.jpa.mysql.EventoConfiguracionJPA;
import com.viaticos.domain.EventoConfiguracionEntity;

@Service
public class EventoConfiguracionRepository implements EventoConfiguracionPort {
	
	@Autowired
    private EventoConfiguracionJPA evconJpa;

	@Override
	public EventoConfiguracionEntity obtenerEventoPorId(int id) {
		return evconJpa.findById(id);
	}

	@Override
	public EventoConfiguracionEntity obtenerEventoPorEvento(String evento) {
		return evconJpa.findByEvento(evento);
	}

	@Override
	public List<EventoConfiguracionEntity> obtenerEvento() {
		return evconJpa.findAll();
	}

	@Override
	public void insertarEvento(EventoConfiguracionEntity evento) {
		evconJpa.save(evento);
		
	}
	
	public void insertarEventoCompleto(String evento, String texto,String usuario) {
		EventoConfiguracionEntity e = new EventoConfiguracionEntity();
		e.setEvento(evento);
		e.setTexto(texto);
		e.setFecha(new Date());
		e.setUsuario(usuario);
		evconJpa.save(e);
	}
	

}
