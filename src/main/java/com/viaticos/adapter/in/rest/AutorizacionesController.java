package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.ApiException;
import com.viaticos.application.port.in.AutorizacionesUseCase;
import com.viaticos.domain.Solicitud;

@RequestMapping("/autorizaciones")
@RestController
public class AutorizacionesController {

	@Autowired
	private AutorizacionesUseCase useCase;

	@GetMapping("")
	public List<Solicitud> obtenerSolicitudesPendientesDeAutorizarAprobador() {
		return null;
	}

	@GetMapping("/usuarios/{usuario}/solicitudes-pendientes-autorizar")
	public List<Solicitud> obtenerSolicitudesPendientesDeAutorizacionDelContador(@PathVariable String usuario) {
		return useCase.consultarSolicitudesDeUsuarioPorEstatus(usuario, "2");
	}

	@PutMapping("solicitudes-viaticos/{solicitud}")
	public void aprobarRechazarSolicitud(@RequestParam(value = "usuario") String usuario,
			@PathVariable("solicitud") String numeroSolicitud, 
			@RequestParam(value = "tipo") String tipo,
			@RequestParam(value = "motivo", required = false) String motivo) {
		switch (tipo) {
		case "AUTORIZAR":
			useCase.autorizarSolicitud(usuario, numeroSolicitud, motivo);
			break;
		case "RECHAZAR":
			useCase.rechazarSolicitud(usuario, numeroSolicitud, motivo);
			break;
		default:
			throw new ApiException(500, "Es necesario ingresar el tipo de movimiento");
		}

	}

}
