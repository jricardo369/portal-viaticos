package com.viaticos.adapter.in.rest;

import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.ApiException;
import com.viaticos.application.port.in.AprobacionesUseCase;
import com.viaticos.domain.Solicitud;

@RequestMapping("/aprobaciones")
@RestController
public class AprobacionesController {

	@Autowired
	private AprobacionesUseCase useCase;

	@GetMapping("/usuarios/{usuario}/comprobaciones-pendientes-autorizar")
	public List<Solicitud> obtenerComprobacionesPendientesDeAprobarPorUsuario(@PathVariable String usuario) {
		return useCase.consultarComprobacionesPendientesDeUsuarioPorEstatus(usuario, "");
	}

	@PutMapping("/comprobantes-de-viaticos/solicitud/{solicitud}")
	public void aprobarORechazarComprobacion(@PathVariable("solicitud") int solicitud,
			@RequestParam("tipo") String tipo, @RequestParam(value = "motivo", required = false) String motivo,
			@RequestParam(value = "usuario") String usuario) {
		switch (tipo) {
		case "AUTORIZAR":
			useCase.autorizarComprobacion(usuario, solicitud, motivo);
			break;
		case "RECHAZAR":
			useCase.rechazarComprobacion(usuario, solicitud, motivo);
			break;
		default:
			throw new ApiException(500, "Es necesario ingresar el tipo de movimiento");
		}

	}

	@PutMapping("/comprobantes-de-viaticos/solicitudes")
	public void aprobarORechazarComprobacionMasivo(@RequestParam("solicitudes") String solicitudes,
			@RequestParam("tipo") String tipo, @RequestParam(value = "motivo", required = false) String motivo,
			@RequestParam(value = "usuario") String usuario) {

		int solicitud = 0;
		StringTokenizer st = new StringTokenizer(solicitudes, ",");
		while (st.hasMoreTokens()) {

			solicitud = Integer.valueOf(st.nextToken());

			// Validar que tipo será
			if (tipo.equals("AUTORIZAR")) {
				useCase.autorizarComprobacion(usuario, solicitud, motivo);
			}
			if (tipo.equals("RECHAZAR")) {
				useCase.rechazarComprobacion(usuario, solicitud, motivo);
			}

		}

	}

	@PutMapping("/comprobantes-de-viaticos/comprobante/{comprobante}")
	public void aprobarOrechazarComprobante(@PathVariable("comprobante") int idComprobante,
			@RequestParam("tipo") String tipo, @RequestParam(value = "motivo", required = false) String motivo,
			@RequestParam(value = "usuario") String usuario) {
		switch (tipo) {
		case "AUTORIZAR":
			useCase.autorizarComprobante(idComprobante, tipo, usuario);
			break;
		case "RECHAZAR":
			useCase.rechazarComprobante(idComprobante, motivo, tipo, usuario);
			break;
		default:
			throw new ApiException(500, "Es necesario ingresar el tipo de movimiento");
		}

	}

}
