package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.DivisionEntity;

public interface DivisionUseCase {
	
	public List<DivisionEntity> obtenerDivisiones();
	public DivisionEntity obtenerDivisionPorDivision(String division);
	public void insertarDivision(DivisionEntity division);
	public void eliminarDivision(String division);
	public void modificarDivision(DivisionEntity division);

}
