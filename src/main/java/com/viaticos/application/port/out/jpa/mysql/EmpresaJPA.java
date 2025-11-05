package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viaticos.domain.EmpresaEntity;

@Repository
public interface EmpresaJPA extends JpaRepository<EmpresaEntity, Serializable> {
	
	public List<EmpresaEntity> findAll();
	public EmpresaEntity findById(int id);
	public EmpresaEntity findByEmpresa(String empresa);
	
	@Query(value = "SELECT * FROM empresa WHERE find_in_set(:codigoEmpresa,codigo_empresa)",nativeQuery = true)
	public EmpresaEntity obtenerPorCodigoEmpresa(@Param("codigoEmpresa")String codigoEmpresa);
	
	@Query(value = "SELECT * FROM empresa WHERE codigo_empresa in(:codigoEmpresa)",nativeQuery = true)
	public EmpresaEntity obtenerPorCodigoEmpresaCompleto(@Param("codigoEmpresa")String codigoEmpresa);
	
	
	@Query(value = "SELECT * FROM empresa WHERE codigo_empresa like %:codigoEmpresa%",nativeQuery = true)
	public List<EmpresaEntity> obtenerEmpresasPorCodigo(@Param("codigoEmpresa")String codigoEmpresa);

}
