package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viaticos.domain.EmpresaAprobacionEntity;

@Repository
public interface EmpresaAprobacionJPA extends JpaRepository<EmpresaAprobacionEntity, Serializable> {
	
	public List<EmpresaAprobacionEntity> findAll();
	public EmpresaAprobacionEntity findById(int id);
	public EmpresaAprobacionEntity findByEmpresa(String empresa);
	
	@Query(value = "SELECT * FROM empresa_aprobacion WHERE find_in_set(:idEmpAprob,id_empresa_aprobacion)",nativeQuery = true)
	public EmpresaAprobacionEntity obtenerPorIdEmpAp(@Param("idEmpAprob")int idEmpAprob);
	
	@Query(value = "SELECT * FROM empresa WHERE codigo_empresa like %:codigoEmpresa%",nativeQuery = true)
	public List<EmpresaAprobacionEntity> obtenerEmpresasPorCodigo(@Param("codigoEmpresa")String codigoEmpresa);

}
