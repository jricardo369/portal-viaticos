package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.EventoConfiguracionUseCase;
import com.viaticos.domain.EventoConfiguracionEntity;

@RequestMapping("/evento_configuracion")
@RestController
public class EventoConfiguracionController {
	
	@Autowired
	private EventoConfiguracionUseCase eventoUsecase;
	
	@GetMapping("{evento}")
	public EventoConfiguracionEntity obtenerEvento(@PathVariable String evento) {
		return eventoUsecase.obtenerEvento(evento);
	}
	
	@GetMapping
	public List<EventoConfiguracionEntity> obtenerEvento() {
		return eventoUsecase.obtenerevento();
	}
	
	@PostMapping
	public void insertarEvento(@RequestBody EventoConfiguracionEntity evento) {
		eventoUsecase.insertarEvento(evento);
	}

}
