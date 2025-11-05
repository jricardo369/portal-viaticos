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
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.EmpresaUseCase;
import com.viaticos.domain.EmpresaEntity;

@RequestMapping("/empresa")
@RestController
public class EmpresaController {
	
	@Autowired
	private EmpresaUseCase empresaUsecase;
	
	@GetMapping("{empresa}")
	public EmpresaEntity obtenerEmpresa(@PathVariable String empresa) {
		return empresaUsecase.obtenerEmpresaCompleto(empresa);
	}
	
	@GetMapping
	public List<EmpresaEntity> obtenerEmpresa() {
		return empresaUsecase.obtenerEmpresa();
	}
	
	@PostMapping
	public void insertarEmpresa(@RequestBody EmpresaEntity empresa) {
		empresaUsecase.insertarEmpresa(empresa);
	}
	
	@PutMapping
	public void modificarEmpresa(@RequestBody EmpresaEntity empresa) {
		empresaUsecase.modificarEmpresa(empresa);
	}
	
	@DeleteMapping("{id}")
	public void eliminarEmpresa(@PathVariable int id) {
		empresaUsecase.eliminarEmpresa(id);
	}

}
