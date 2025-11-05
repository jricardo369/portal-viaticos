package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.Jobs;

public interface JobFOXPort {
	
	public String generarPolizaFox(int numeroSolicitud, boolean guardar);
	public int tomarUltimoValorCabecera();
	public String leerTabla(String tabla);
	public List<Jobs> generaPolizaFoxPhp();
	
}
