package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.SolicitudViaticosEntity;

public interface PrepolizaUseCase {
	
	public List<Prepoliza> generarPoliza(int numeroSolicitud,boolean tabla);
	public List<Prepoliza> generarPolizaDetalle(List<ComprobanteViaticoEntity> comprobantes, SolicitudViaticosEntity solicitud,boolean tabla);

}
