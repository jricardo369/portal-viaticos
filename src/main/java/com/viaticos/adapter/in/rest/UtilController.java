package com.viaticos.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.viaticos.application.port.in.ViaticosDeUsuarioUseCase;

@RequestMapping("/util")
@RestController
public class UtilController {
	
	Logger log = LoggerFactory.getLogger(UtilController.class);

	@Autowired
	private ViaticosDeUsuarioUseCase viaticosUsUseCase;

	@GetMapping("/alta-disp-preuebas/{idSolicitud}")
	public boolean altaDispersionParaPruebas(@PathVariable("idSolicitud") int idSolicitud) {
		viaticosUsUseCase.altaDispercionParaPruebas(idSolicitud);
		
		return true;
	}
	
	@GetMapping("/cambiar-estatus-solicitud/{idSolicitud}")
	public boolean cambiarEstatusSolicitd(@PathVariable("idSolicitud") int idSolicitud,
			@RequestParam("estatus") int estatus) {
		viaticosUsUseCase.actualizaEstatusSolicitud(idSolicitud, estatus);
		
		return true;
	}

	

}
