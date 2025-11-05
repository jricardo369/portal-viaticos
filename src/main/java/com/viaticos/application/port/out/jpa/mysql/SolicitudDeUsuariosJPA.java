package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Repository
public interface SolicitudDeUsuariosJPA extends JpaRepository<SolicitudViaticosEntity, Serializable> {

	@Query(value = "SELECT * FROM solicitud_de_viaticos WHERE usuario =:usuario AND estatus IN (:estatus)", nativeQuery = true)
	public List<SolicitudViaticosEntity> encuentraUsuarioEstatuslista(@Param("usuario") String usuario,
			@Param("estatus") List<EstatusSolicitudEntity> estatus);

	public abstract SolicitudViaticosEntity findBynumeroSolicitud(int numeroSolicitud);

	@Query(value = "SELECT * FROM solicitud_de_viaticos WHERE empresa IN (:empresas) AND estatus IN (:estatus)", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerPorEmpresasYEstatus(@Param("empresas") List<String> empresas,
			@Param("estatus") List<EstatusSolicitudEntity> estatus);

	@Query(value = "SELECT * FROM solicitud_de_viaticos WHERE empresa IN (:empresas) AND estatus IN (:estatus) AND "
			+ "numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico WHERE evento LIKE CONCAT('%', 'director', '%') ) ", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerPorEmpresasYEstatusYDirector(@Param("empresas") List<String> empresas,
			@Param("estatus") List<EstatusSolicitudEntity> estatus);
	
	@Query(value = " SELECT * FROM solicitud_de_viaticos"
			+ " WHERE numero_solicitud IN"
			+ " (SELECT numero_solicitud FROM evento_de_viatico WHERE evento = ?1 "
			+ " AND date(fecha) BETWEEN ?2 AND ?3)", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerPorEventoYFechas(String evento,String fechaInicio,String fechaFin);
	
	@Query(value = " SELECT * FROM solicitud_de_viaticos"
			+ " WHERE numero_solicitud NOT IN"
			+ " (SELECT numero_solicitud FROM evento_de_viatico WHERE evento = ?1 "
			+ " AND date(fecha) BETWEEN ?2 AND ?3)", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerPorNoEventoYFechas(String evento,String fechaInicio,String fechaFin);
	
	@Query(value = " SELECT * FROM solicitud_de_viaticos"
			+ " WHERE numero_solicitud NOT IN"
			+ " (SELECT numero_solicitud FROM evento_de_viatico WHERE evento = ?1 "
			+ " AND date(fecha) BETWEEN ?2 AND ?3) and estatus = ?4", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerPorNoEventoYFechasYEstatus(String evento,String fechaInicio,String fechaFin,int estatus);
	
	@Query(value = " SELECT * FROM solicitud_de_viaticos"
			+ " WHERE numero_solicitud = ?1", nativeQuery = true)
	public List<SolicitudViaticosEntity> ObtenerSoloSolicitudPorNumeroSol(int numeroSolicitud);
	
	@Transactional
	@Modifying
	@Query(value = "INSERT INTO solicitud_de_viaticos (numero_solicitud, motivo, fecha_inicio, fecha_fin, anticipo, usuario, "
			+ "empresa, ceco, concepto, observaciones, estatus) "
			+ "VALUES(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11)", nativeQuery = true)
	public void creaSolicitud(int numeroSolicitud, String motivo, Date fechaInicio, Date fechaFin, BigDecimal anticipo,
			String usuario, String empresa, String ceco, String concepto, String observaciones,
			EstatusSolicitudEntity estatus);

	@Query(value = "SELECT * FROM solicitud_de_viaticos WHERE empresa IN (:empresas) AND estatus =:estatus", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEmpresaAndEstatus(@Param("empresas") List<String> empresas,
			@Param("estatus") EstatusSolicitudEntity estatus);

	@Transactional
	@Modifying
	@Query(value = "UPDATE solicitud_de_viaticos SET " + "estatus = ?2 WHERE numero_solicitud = ?1", nativeQuery = true)
	public void actualizaEstatusPeticion(int solicitud, EstatusSolicitudEntity estatus);

	@Query(value = "SELECT * FROM solicitud_de_viaticos WHERE estatus IN (:estatus)", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEstatus(List<EstatusSolicitudEntity> estatus);

	@Query(value = "SELECT * FROM solicitud_de_viaticos s WHERE numero_solicitud NOT IN"
			+ "(SELECT s.numero_solicitud " + "FROM solicitud_de_viaticos s, evento_de_viatico e "
			+ "WHERE e.numero_solicitud = s.numero_solicitud and e.evento "
			+ "IN(:evento) group by numero_solicitud) AND estatus = 14", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEvento(String evento);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos s WHERE estatus = :estatus "
			+ "AND numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN (:evento) )  order by s.usuario", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEventoYEstatus(String evento,String estatus);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos s WHERE numero_solicitud = :sol "
			+ "AND numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN (:evento) )", nativeQuery = true)
	public SolicitudViaticosEntity obtenerEventoPorSolicitud(String evento,int sol);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos s "
			+ " LEFT JOIN empresa e ON e.codigo_empresa like CONCAT('%', RTRIM(s.empresa), '%') "
			+ "WHERE numero_solicitud NOT IN"
			+ "(SELECT s.numero_solicitud " + "FROM solicitud_de_viaticos s, evento_de_viatico e "
			+ "WHERE e.numero_solicitud = s.numero_solicitud and e.evento "
			+ "IN(:evento) group by numero_solicitud) AND estatus = 14 AND e.sistema = :sistema", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEventoPorSistema(String evento,String sistema);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos s "
			+ "JOIN empresa e ON find_in_set(RTRIM(s.empresa),codigo_empresa) "
			+ "WHERE estatus = :estatus AND e.sistema = :sistema "
			+ "AND numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN (:evento))  order by s.empresa,s.usuario DESC;", nativeQuery = true)
	public List<SolicitudViaticosEntity> findByEventoPorSistemaYEstatus(String evento,String sistema,String estatus);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos s "
			+ "JOIN empresa e ON find_in_set(RTRIM(s.empresa),codigo_empresa) "
			+ "WHERE estatus = :estatus AND e.sistema NOT IN(:sistema)  "
			+ "AND numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento IN (:evento))  order by s.empresa,s.usuario DESC;", nativeQuery = true)
	public List<SolicitudViaticosEntity>  findByEventoPorSistemaYEstatusNotIn(String evento,String sistema,String estatus);

	@Query(value = "SELECT * FROM solicitud_de_viaticos "
			+ "WHERE estatus IN(:estatus) AND fecha_creacion BETWEEN :fechaInicio AND :fechaFin "
			+ " AND empresa IN(:empresas)", nativeQuery = true)
	public List<SolicitudViaticosEntity> findForReporte(@Param("estatus") List<String> estatus,
			@Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin,
			@Param("empresas") List<String> empresas);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos "
			+ "WHERE estatus IN(:estatus) AND fecha_creacion BETWEEN :fechaInicio AND :fechaFin "
			+ " AND empresa IN(:empresas) AND "
			+ "numero_solicitud NOT IN ( SELECT numero_solicitud FROM evento_de_viatico "
			+ "WHERE evento LIKE CONCAT('%', 'director', '%') ) ", nativeQuery = true)
	public List<SolicitudViaticosEntity> findForReporteDirector(@Param("estatus") List<String> estatus,
			@Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin,
			@Param("empresas") List<String> empresas);
	
	@Query(value = "SELECT * FROM solicitud_de_viaticos "
			+ "WHERE CURDATE() > DATE_ADD(fecha_fin,INTERVAL :dias DAY) "
			+ "AND estatus NOT IN(1,2,4,6,9,11,12,14,15);", nativeQuery = true)
	public List<SolicitudViaticosEntity> obtenerSolicitudesFueraRango(int dias);

}
