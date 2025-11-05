package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

public interface ContabilizarFoxPort {
	
	public Object insertarEncabezado(SolicitudViaticosEntity solicitud,int numPoliza);
	public List<Object> insertarComprobantes(List<ComprobanteViaticoEntity> comprobantes,SolicitudViaticosEntity solicitud,int numPoliza);
	public List<Object> insertarNacionales(List<ComprobanteViaticoEntity> comprobantes,SolicitudViaticosEntity solicitud,int numPoliza);
	public List<Object> insertarDoctos(List<ComprobanteViaticoEntity> comprobantes,SolicitudViaticosEntity solicitud,int numPoliza);
	public int obtenerUltimoValorCons();
	public String leerTabla(String tabla);
	
	public Object encabezado();

}
