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
import com.viaticos.application.port.in.AprobacionesDirectorUseCase;
import com.viaticos.domain.Solicitud;

@RequestMapping("/aprobaciones-director")
@RestController
public class AprobacionesDirectorController {

	@Autowired
	private AprobacionesDirectorUseCase useCase;

	@GetMapping("/viajes-pendientes-autorizar")
	public List<Solicitud> obtenerComprobacionesPendientesDeAprobarPorUsuario(@RequestParam String usuario,@RequestParam("empresas") String empresas,@RequestParam("estatus") String estatus,@RequestParam("puedeAprobarSolDir") boolean puedeAprobarSolDir) {
		return useCase.consultarAprobacionesPendientesDeUsuarioPorEstatus(usuario, estatus,empresas,puedeAprobarSolDir);
	}

	@PutMapping("/viajes-de-viaticos/solicitud/{solicitud}")
	public void aprobarORechazarComprobacion(@PathVariable("solicitud") int solicitud,
			@RequestParam("tipo") String tipo, @RequestParam(value = "motivo", required = false) String motivo,
			@RequestParam(value = "usuario") String usuario,@RequestParam("puedeAprobarSolDir") boolean puedeAprobarSolDir) {
		switch (tipo) {
		case "AUTORIZAR":
			useCase.autorizarViaje(usuario, solicitud, motivo,puedeAprobarSolDir);
			break;
		case "RECHAZAR":
			useCase.rechazarViaje(usuario, solicitud, motivo,puedeAprobarSolDir);
			break;
		default:
			throw new ApiException(500, "Es necesario ingresar el tipo de movimiento");
		}

	}

}
