package com.viaticos.adapter.in.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.UsuariosUseCase;
import com.viaticos.domain.Usuario;

@RequestMapping("/usuarios")
@RestController
public class UsuarioController {
	
	@Autowired
	private UsuariosUseCase usuarioUseCase;

	@GetMapping("{usuario}")
	public Usuario obtenerUsuario(@PathVariable("usuario") String usuario) {

		Usuario user = new Usuario();
		
		user = usuarioUseCase.buscaInfoUsuario(usuario);

		
		return user;
	}

}
