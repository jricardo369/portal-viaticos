package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.DivisionEntity;

public interface DivisionPort {
	
	    public List<DivisionEntity> obtenerDivisiones();
		public DivisionEntity obtenerDivisionPorDivision(String division);
		public void insertarDivision(DivisionEntity division);
		public void eliminarDivision(String division);
		public void modificarDivision(DivisionEntity division);

}
