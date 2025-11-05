package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.AvisoUseCase;
import com.viaticos.domain.Aviso;

@RequestMapping("/avisos")
@RestController
public class AvisosController {
	
	@Autowired
	private AvisoUseCase avisoController;
	
	@GetMapping("{numSolicitud}")
	public List<Aviso> obtenerComprobacionesPendientesDeAprobarPorUsuario(@PathVariable int numSolicitud) {
		return avisoController.obtenerAvisosDeSolicitud(numSolicitud);
	}

}
