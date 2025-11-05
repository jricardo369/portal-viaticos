package com.viaticos.adapter.out.sql;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.viaticos.adapter.out.sql.wrappers.ComprobanteRM;
import com.viaticos.application.port.out.ComprobantesDeViaticosPort;
import com.viaticos.application.port.out.jpa.mysql.ComprobanteViaticoJPA;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Service
public class ComprobanteDeViaticosRepository implements ComprobantesDeViaticosPort {

	@Autowired
	private ComprobanteViaticoJPA comprobanteJpa;

	@Override
	public ComprobanteViaticoEntity cargaDeComprobante(ComprobanteViaticoEntity c) {
		// Insertar en comprobante

		ComprobanteViaticoEntity comprobante = comprobanteJpa.save(c);
		return comprobante;
	}

	@Override
	public ComprobanteViaticoEntity obtenerDeComprobante(int idComprobante) {

		return comprobanteJpa.findById(idComprobante);
	}

	@Override
	public void modificarDeComprobante(ComprobanteViaticoEntity c) {

		comprobanteJpa.save(c);

	}

	@Override
	public void eliminarDeComprobante(ComprobanteViaticoEntity c) {
		// Eliminar en comprobante
		comprobanteJpa.delete(c);

	}
	
	@Override
	public void actualizaNoAplicaACero(int solicitud) {

		comprobanteJpa.actualizaNoAplicaACero(solicitud);

	}

	@Override
	public void actualizaEstatusComprobante(ComprobanteViaticoEntity c) {
		comprobanteJpa.save(c);

	}

	@Override
	public void solicitaAprobacionDeComprobanteEstatus(int comprobante, String estatus) {
		comprobanteJpa.actualizaEstatusEnviaPeticion(comprobante, estatus);

	}
	
	@Override
	public void actualizaMontoNoAplica(int idComprobante,BigDecimal noAplica) {
		comprobanteJpa.actualizaMontoNoAplica(idComprobante, noAplica);

	}

	@Override
	public void guardaEstatusComprobacionesDeSolicitud(List<ComprobanteViaticoEntity> comprobantes) {
		comprobanteJpa.saveAll(comprobantes);
	}

	@Override
	public List<ComprobanteViaticoEntity> obtenerComprobantesPorDia(Date fechaCarga,
			SolicitudViaticosEntity solicitud) {

		return comprobanteJpa.obtenerPorFechaSolicitud(fechaCarga, solicitud);
	}

	@Override
	public void actualizaAprobacionNoAplica(int comprobante, boolean aprobacion) {
		comprobanteJpa.actualizaAprobacionNoAplica(comprobante, aprobacion);

	}
	
	@Override
	public void actualizaMontoADescontar(int comprobante, BigDecimal montoADescontar) {
		comprobanteJpa.actualizaMontoADescontar(comprobante, montoADescontar);

	}

	@Override
	public List<ComprobanteViaticoEntity> obtenerComprobantesPorDiaYTipogasto(Date fechaCarga,
			SolicitudViaticosEntity solicitud, String subCuentaDescripcion) {
		
		
		
		return comprobanteJpa.obtenerPorFechaSolicitudTipoGasto(fechaCarga, solicitud, subCuentaDescripcion);
	}
	
	@Override
	public BigDecimal obtenerPropinaComprobantesPorDiaYTipogasto(Date fechaCarga,
			SolicitudViaticosEntity solicitud, String subCuentaDescripcion) {

		BigDecimal salida = comprobanteJpa.obtenerPropinaPorFechaSolicitudTipoGasto(fechaCarga, solicitud, subCuentaDescripcion);
		if(salida == null) {
			return BigDecimal.ZERO;
		}
		return salida;
	}

}
