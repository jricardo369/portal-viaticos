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

import com.viaticos.domain.CfdiEntity;

@Repository
public interface CfdiJPA extends JpaRepository<CfdiEntity, Serializable> {
	
	public CfdiEntity findByUuid(String uuid);
	
	@Query(value = "SELECT sum(c.total) from comprobante_viatico c "
			+ "JOIN sub_cuenta_contable s on s.id_subcuenta_contable = c.sub_cuenta_contable "
			+ "WHERE s.codigo IN (?1) AND c.numero_solicitud = ?2 "
			+ "AND c.fecha_carga = ?3", nativeQuery = true)
	public BigDecimal totalPorTipoSubCuentaYFecha(String codigo,int numeroSolicitud,Date fecha);
	
	@Query(value = "SELECT sum(c.total) from comprobante_viatico c "
			+ "JOIN sub_cuenta_contable s on s.id_subcuenta_contable = c.sub_cuenta_contable "
			+ "WHERE s.descripcion like %?1% AND c.numero_solicitud = ?2 "
			+ "AND c.fecha_carga = ?3", nativeQuery = true)
	public BigDecimal totalPorNombreSubCuentaYFecha(String nombre,int numeroSolicitud,Date fecha);
	
	@Query(value = "select sum(c.total) from comprobante_viatico c\r\n"
			+ "join sub_cuenta_contable s on s.id_subcuenta_contable = c.sub_cuenta_contable\r\n"
			+ "where s.codigo IN (?1) AND c.numero_solicitud = ?2", nativeQuery = true)
	public BigDecimal totalPorTipoSubCuenta(String codigo,int numeroSolicitud);
	
	@Query(value = "select id_comprobante_viatico from comprobante_viatico c\r\n"
			+ "join sub_cuenta_contable s on s.id_subcuenta_contable = c.sub_cuenta_contable\r\n"
			+ "where s.descripcion like %?2% AND c.numero_solicitud = ?1", nativeQuery = true)
	public BigDecimal totalPorNombre(String codigo,int numeroSolicitud);
	
	@Query(value = "select sum(cf.total) from comprobante_viatico c\r\n"
			+ "LEFT JOIN cfdi cf on cf.id_comprobante_viatico = c.id_comprobante_viatico\r\n"
			+ "JOIN sub_cuenta_contable s on s.id_subcuenta_contable = c.sub_cuenta_contable\r\n"
			+ "WHERE s.codigo NOT IN (?1) AND c.numero_solicitud = ?2", nativeQuery = true)
	public BigDecimal totalPorTipoSubCuentaNotIn(String codigo,int numeroSolicitud);
	
	@Query(value = "SELECT SUM(propina) "
			+ "FROM comprobante_viatico where numero_solicitud = ?1", nativeQuery = true)
	public BigDecimal totalPropina(int numeroSolicitud);
	
	@Query(value = "SELECT sum(propina)+sum(total) "
			+ "FROM comprobante_viatico where numero_solicitud = ?1", nativeQuery = true)
	public BigDecimal totalComprobado(int numeroSolicitud);
	
	@Query(value = "SELECT sum(total) "
			+ "FROM comprobante_viatico where numero_solicitud = ?1", nativeQuery = true)
	public BigDecimal totalComprobadoSinPropina(int numeroSolicitud);
	
	@Query(value = "SELECT sum(total) "
			+ "FROM comprobante_viatico WHERE numero_solicitud = ?1 AND ruta_xml = '' AND tipo_gasto not in('reintegro')", nativeQuery = true)
	public BigDecimal totalComprobadoNoDeducible(int numeroSolicitud);
	
	@Query(value = "SELECT sum(total) "
			+ "FROM comprobante_viatico WHERE numero_solicitud = ?1 AND tipo_gasto IN('reintegro')", nativeQuery = true)
	public BigDecimal totalReintegroSolicitud(int numeroSolicitud);
	
	@Query(value = "SELECT sum(total) "
			+ "FROM comprobante_viatico WHERE numero_solicitud = ?1 AND ruta_xml <> ''", nativeQuery = true)
	public BigDecimal totalComprobadoDeducible(int numeroSolicitud);
	
	@Query(value = "SELECT c.total,sc.codigo,cf.moneda,sc.descripcion, c.propina "
			+ "FROM comprobante_viatico c "
			+ "LEFT JOIN sub_cuenta_contable sc on sc.id_subcuenta_contable = c.sub_cuenta_contable "
			+ "LEFT JOIN cfdi cf on cf.id_comprobante_viatico = c.id_comprobante_viatico "
			+ "WHERE c.fecha_carga =?1 and numero_solicitud = ?2 GROUP BY tipo_gasto,codigo", nativeQuery = true)
	public List<String> totalesDeFecha(Date fecha,int numeroSolicitud);

	@Query(value = "SELECT SUM(c.total) FROM comprobante_viatico c "
			+ "JOIN solicitud_de_viaticos sol ON sol.numero_solicitud = c.numero_solicitud "
			+ "WHERE YEAR(c.fecha_carga) = ?1 "
			+ "AND sol.usuario = ?2 "
			+ "AND tipo_gasto not in('reintegro') "
			+ "AND c.ruta_xml = '' "
			+ "AND sol.numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN ('Comprobación') AND sol.usuario = ?2);"
			+ "" , nativeQuery = true)
	public BigDecimal totalTotalNoDeduciblePorAnio(String ejercicio,String usuario);
	
	@Query(value = "SELECT SUM(c.propina) FROM comprobante_viatico c "
			+ "JOIN solicitud_de_viaticos sol ON sol.numero_solicitud = c.numero_solicitud "
			+ "WHERE YEAR(c.fecha_carga) = ?1 "
			+ "AND sol.usuario = ?2 "
			+ "AND tipo_gasto not in('reintegro') "
			+ "AND sol.numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN ('Comprobación') AND sol.usuario = ?2);"
			+ "" , nativeQuery = true)
	public BigDecimal totalTotalPropinaNoDeduciblePorAnio(String ejercicio,String usuario);
	
	@Query(value = "SELECT sum(no_aplica) FROM comprobante_viatico "
			+ "WHERE numero_solicitud = ?1 "
			+ "AND no_aplica <> '0' AND aprobacion_no_aplica = ?2 " , nativeQuery = true)
	public BigDecimal totalNoAplica(int numeroSolicitud,String aprobacionNoAplica);
	
	@Query(value = "SELECT sum(no_aplica) FROM comprobante_viatico "
			+ "WHERE numero_solicitud = ?1 "
			+ "AND no_aplica <> '0' AND aprobacion_no_aplica = ?2  AND id_comprobante_viatico = ?3 " , nativeQuery = true)
	public BigDecimal totalNoAplicaPorComprobante(int numeroSolicitud,String aprobacionNoAplica,int idComprobante);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE cfdi SET "
			+ "ish = ?2 WHERE id_cfdi = ?1", nativeQuery = true)
	public void actulizaISHCfdi(int idCfdi, BigDecimal ish);

}
