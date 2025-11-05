package com.viaticos.adapter.in.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.GenerarTokenDeAutenticacionUseCase;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET,RequestMethod.PUT,RequestMethod.POST,RequestMethod.OPTIONS})
@RequestMapping("/autenticaciones")
@RestController
public class AutenticationRestController {
	
	@Autowired
	private GenerarTokenDeAutenticacionUseCase useCase;

	Logger log = LoggerFactory.getLogger(AutenticationRestController.class);
	
	@PostMapping
	public String login(@RequestParam("username") String username, @RequestParam("password") String pwd) { 
		
		log.info("Autenticando usuario");
		log.info("usuario:"+username);
		return useCase.autenticarUsuario(username, pwd);	
		
	}
	
	
	
}
