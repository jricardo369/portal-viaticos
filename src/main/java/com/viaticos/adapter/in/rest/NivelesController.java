package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.NivelesUseCase;
import com.viaticos.domain.NivelTopeUsuarioEntity;

@RequestMapping("/niveles")
@RestController
public class NivelesController{
	
	@Autowired
	private NivelesUseCase nivelesUseCase;
	

	@GetMapping("{nivel}")
	public NivelTopeUsuarioEntity obtenerNivel(@PathVariable int nivel) {
		return nivelesUseCase.obtenerNivel(nivel);
	}
	
	@GetMapping
	public List<NivelTopeUsuarioEntity> obtenerNiveles() {
		return nivelesUseCase.obtenerNiveles();
	}
	
	@PostMapping
	public void insertarNivel(@RequestBody NivelTopeUsuarioEntity nivel) {
		nivelesUseCase.insertarNivel(nivel);
	}
	
	@PutMapping
	public void modificarNivel(@RequestBody NivelTopeUsuarioEntity nivel,@RequestParam String usuario) {
		nivelesUseCase.modificarNivel(nivel,usuario);
	}
	
	@DeleteMapping("{nivel}")
	public void eliminarNivel(@PathVariable int nivel,@RequestParam String usuario) {
		nivelesUseCase.eliminarNivel(nivel,usuario);
	}
	
	


}
