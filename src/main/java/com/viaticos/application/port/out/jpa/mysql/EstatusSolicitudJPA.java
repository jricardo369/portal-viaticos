package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.EstatusSolicitudEntity;

@Repository
public interface EstatusSolicitudJPA extends JpaRepository<EstatusSolicitudEntity, Serializable>{
	
	public EstatusSolicitudEntity findById(int id);
	
	@Query(value = "SELECT * FROM estatus_solicitud WHERE id_estatus IN (:estatus)", nativeQuery = true)
	public List<EstatusSolicitudEntity> buscaEstatusList(@Param("estatus") List<Integer> estatus);
	
	@Query(value = "SELECT id_estatus,descripcion FROM estatus_solicitud", nativeQuery = true)
	public List<EstatusSolicitudEntity> obtenerEstatus();

}
