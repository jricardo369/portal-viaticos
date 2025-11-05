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

import com.viaticos.application.port.in.DivisionUseCase;
import com.viaticos.domain.DivisionEntity;

@RequestMapping("/division")
@RestController
public class DivisionController {
	
	@Autowired
	private DivisionUseCase divisionUsecase;
	
	@GetMapping("{division}")
	public DivisionEntity obtenerDivision(@PathVariable String division) {
		return divisionUsecase.obtenerDivisionPorDivision(division);
	}
	
	@GetMapping
	public List<DivisionEntity> obtenerDivisiones() {
		return divisionUsecase.obtenerDivisiones();
	}
	
	@PostMapping
	public void insertarDivision(@RequestBody DivisionEntity division) {
		divisionUsecase.insertarDivision(division);
	}
	
	@PutMapping
	public void modificarDivision(@RequestBody DivisionEntity division) {
		divisionUsecase.modificarDivision(division);
	}
	
	@DeleteMapping("{id}")
	public void eliminarDivision(@PathVariable String id) {
		divisionUsecase.eliminarDivision(id);
	}

}
