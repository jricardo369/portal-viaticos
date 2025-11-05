package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.DivisionPort;
import com.viaticos.application.port.out.jpa.mysql.DivisionJPA;
import com.viaticos.domain.DivisionEntity;

@Service
public class DivisionRepository implements DivisionPort {
	
	@Autowired
	private DivisionJPA enpJpa;
	
	
	@Override
	public DivisionEntity obtenerDivisionPorDivision(String division) {
		return enpJpa.obtenerPorDivision(division);
	}

	@Override
	public List<DivisionEntity> obtenerDivisiones() {
		return enpJpa.findAll();
	}

	@Override
	public void insertarDivision(DivisionEntity division) {
		enpJpa.save(division);
		
	}

	@Override
	public void eliminarDivision(String division) {
		enpJpa.deleteById(division);
		
	}

	@Override
	public void modificarDivision(DivisionEntity division) {
		enpJpa.save(division);
	}

}
