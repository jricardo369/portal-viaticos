package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.layoutComprobacion;

public interface JobPort {
	
	public String generarLayoutCargaEntrega(String fecha,boolean guardar,int numeroSolicitud);
	public String generarLayoutCargaComprobacion(String fecha,boolean guardar,int numeroSolicitud);
	public String obtenerAgrupado(List<layoutComprobacion> list, int referencia,int tipo);
	public void guardarLog(int tipo,StringBuilder sb);
	public void agregarEvento(String evento, String texto, SolicitudViaticosEntity solicitud);
	public String limpiarSolicitudesFueraRango(int numeroSolicitud,boolean guardar);
	
}
