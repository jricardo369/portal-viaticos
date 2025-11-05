package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Repository
public interface ComprobanteViaticoJPA extends JpaRepository<ComprobanteViaticoEntity, Serializable> {

	public ComprobanteViaticoEntity findById(int id);

	/*
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET "
			+ "estatus_comprobante = ?1, observaciones = ?3 WHERE id_comprobante_viatico = ?2", nativeQuery = true)
	public void actualizaEstatusComprobante(String estatus, int idComprobante, String motivo);
	*/
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET "
			+ "estatus_comprobante = ?2 WHERE id_comprobante_viatico = ?1", nativeQuery = true)
	public void actualizaEstatusEnviaPeticion(int idComprobante, String estatus);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET " + "no_aplica = 0 WHERE numero_solicitud = ?1", nativeQuery = true)
	public void actualizaNoAplicaACero(int solicitud);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET " + "no_aplica = ?2 WHERE id_comprobante_viatico = ?1", nativeQuery = true)
	public void actualizaMontoNoAplica(int idComprobante,BigDecimal noAplica);

	@Query(value = "SELECT fecha_carga FROM comprobante_viatico WHERE numero_solicitud = ?1 GROUP BY fecha_carga", nativeQuery = true)
	public List<String> findFechasComprobantes(int numeroSolicitud);
	
	@Query(value = "SELECT * FROM comprobante_viatico WHERE numero_solicitud = ?2 AND fecha_carga = ?1",nativeQuery = true)
	public List<ComprobanteViaticoEntity> obtenerPorFechaSolicitud(Date fecha, SolicitudViaticosEntity solicitud);
	
	@Query(value = "SELECT * FROM comprobante_viatico WHERE numero_solicitud = ?2 AND fecha_carga = ?1 AND tipo_gasto = ?3",nativeQuery = true)
	public List<ComprobanteViaticoEntity> obtenerPorFechaSolicitudTipoGasto(Date fecha, SolicitudViaticosEntity solicitud, String tipo);
	
	@Query(value = "SELECT SUM(no_aplica) FROM comprobante_viatico WHERE numero_solicitud = ?2 AND fecha_carga = ?1 AND tipo_gasto = ?3",nativeQuery = true)
	public BigDecimal obtenerPropinaPorFechaSolicitudTipoGasto(Date fecha, SolicitudViaticosEntity solicitud, String tipo);
	
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET "
			+ "aprobacion_no_aplica = ?2 WHERE id_comprobante_viatico = ?1", nativeQuery = true)
	public void actualizaAprobacionNoAplica(int comprobante, boolean aprobacion);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE comprobante_viatico SET "
			+ "monto_aprobado = ?2 WHERE id_comprobante_viatico = ?1", nativeQuery = true)
	public void actualizaMontoADescontar(int comprobante, BigDecimal aprobacion);
	
	
	
}
