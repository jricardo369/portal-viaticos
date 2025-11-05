package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.SolicitudViaticosEntity;

public interface JobSAPB1Port {
	
	
	public String generarPolizaSAPB1(int numeroSolicitud,boolean guardar);
	public void envioDispercionASAPB1(List<SolicitudViaticosEntity> solicitudes, boolean guardar,StringBuilder logBuilder);
	
}
