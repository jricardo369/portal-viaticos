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

import com.viaticos.application.port.in.EmpresaAprobacionUseCase;
import com.viaticos.domain.EmpresaAprobacionEntity;

@RequestMapping("/empresa-aprobacion")
@RestController
public class EmpresaAprobacionController {
	
	@Autowired
	private EmpresaAprobacionUseCase empresaApUsecase;

	
	@GetMapping
	public List<EmpresaAprobacionEntity> obtenerEmpresas() {
		return empresaApUsecase.obtenerEmpresasAprobacion();
	}
	
	@PostMapping
	public void insertarEmpresa(@RequestBody EmpresaAprobacionEntity empresa) {
		empresaApUsecase.insertarEmpresaAprobacion(empresa);
	}
	
	@PutMapping
	public void modificarEmpresa(@RequestBody EmpresaAprobacionEntity empresa) {
		empresaApUsecase.modificarEmpresaAprobacion(empresa);
	}
	
	@DeleteMapping("{id}")
	public void eliminarEmpresa(@PathVariable int id) {
		empresaApUsecase.eliminarEmpresaAprobacion(id);
	}

}
