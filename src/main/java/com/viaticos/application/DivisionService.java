package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.DivisionUseCase;
import com.viaticos.application.port.out.DivisionPort;
import com.viaticos.domain.DivisionEntity;

@Service
public class DivisionService implements DivisionUseCase {
	
	@Autowired
	private DivisionPort divPort;

	@Override
	public List<DivisionEntity> obtenerDivisiones() {
		return divPort.obtenerDivisiones();
	}
	
	@Override
	public DivisionEntity obtenerDivisionPorDivision(String division) {
		return divPort.obtenerDivisionPorDivision(division);
	}

	@Override
	public void insertarDivision(DivisionEntity division) {
		divPort.insertarDivision(division);
		
	}

	@Override
	public void modificarDivision(DivisionEntity division) {
		divPort.modificarDivision(division);
		
	}

	@Override
	public void eliminarDivision(String division) {
		divPort.eliminarDivision(division);
	}

}
