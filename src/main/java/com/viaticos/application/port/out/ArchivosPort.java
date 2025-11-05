package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.ComprobanteViaticoEntity;

public interface ArchivosPort {
	
	public boolean guardarArchivo(byte[] bytes,String rutaComprobantes,String nombre);
	public boolean eliminarArchivo(String ruta);
	public void guardarArchivoDeLayout(String datos,String ruta,String nombre);
	public byte[] obtenerArchivo(String rutaRelativa);
	public byte[] obtenerArchivosSolicitudZip(List<ComprobanteViaticoEntity> comprobantes);
	public String generaRutaArchivo(String anio,String usuario,String numeroSolicitud);

}
