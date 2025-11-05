package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.EventoDeViaticoCase;
import com.viaticos.domain.EventoViaticoEntity;

@RequestMapping("/evento-de-solicitud")
@RestController
public class EventoDeViaticosController{
	
	@Autowired
	private EventoDeViaticoCase eventoCase;
	
	@GetMapping("{numeroSolicitud}")

	public List<EventoViaticoEntity> obtenerEventosDeViaticos(@PathVariable("numeroSolicitud") int numeroSolicitud) {

		return eventoCase.obtenerEventosDeSolicitud(numeroSolicitud);
	}
	
	@PostMapping
	public void insertarEvento(@RequestBody EventoViaticoEntity evento) {
		eventoCase.crearEventoDeSolicitud(evento);
	}
	
}
