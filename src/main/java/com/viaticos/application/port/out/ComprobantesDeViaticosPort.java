package com.viaticos.application.port.out;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

public interface ComprobantesDeViaticosPort {
	
	public ComprobanteViaticoEntity cargaDeComprobante(ComprobanteViaticoEntity comprobante);
	public ComprobanteViaticoEntity obtenerDeComprobante(int idComprobante);
	public void modificarDeComprobante(ComprobanteViaticoEntity c);
	public void eliminarDeComprobante(ComprobanteViaticoEntity c);
	
	public void actualizaAprobacionNoAplica(int comprobante, boolean aprobacion);
	public void actualizaMontoADescontar(int comprobante, BigDecimal montoADescontar);
	public void actualizaEstatusComprobante(ComprobanteViaticoEntity comprobante);
	public void solicitaAprobacionDeComprobanteEstatus(int comprobante, String estatus);
	public void guardaEstatusComprobacionesDeSolicitud(List<ComprobanteViaticoEntity> comprobantes);
	public List<ComprobanteViaticoEntity> obtenerComprobantesPorDia(Date fechaCarga, SolicitudViaticosEntity solicitud);
	public List<ComprobanteViaticoEntity> obtenerComprobantesPorDiaYTipogasto(Date fechaCarga, SolicitudViaticosEntity solicitud, String subCuenta);
	public BigDecimal obtenerPropinaComprobantesPorDiaYTipogasto(Date fechaCarga,
			SolicitudViaticosEntity solicitud, String subCuentaDescripcion);
	public void actualizaMontoNoAplica(int idComprobante,BigDecimal noAplica);
	public void actualizaNoAplicaACero(int solicitud);
	
}
