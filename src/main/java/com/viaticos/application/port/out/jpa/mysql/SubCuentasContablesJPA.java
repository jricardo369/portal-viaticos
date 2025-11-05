package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.SubCuentaContableEntity;

@Repository
public interface SubCuentasContablesJPA extends CrudRepository<SubCuentaContableEntity, Serializable>{

	public List<SubCuentaContableEntity> findAll();
	public SubCuentaContableEntity findById(int codigo);
	
    @Query(value = "SELECT * FROM sub_cuenta_contable WHERE empresa = :empresa AND find_in_set(:ceco,ceco) AND tipo is null",nativeQuery = true)
	public List<SubCuentaContableEntity> obtenerSubCuentaEmpCeco(@Param("empresa")String empresa,@Param("ceco")  String ceco);
	
    @Query(value = "SELECT * FROM sub_cuenta_contable WHERE empresa = :empresa AND find_in_set(:ceco,ceco) AND tipo = :tipo ",nativeQuery = true)
	public SubCuentaContableEntity obtenerSubCuentaPorTipo(@Param("empresa")String empresa,@Param("ceco")  String ceco,@Param("tipo")  String tipo);
    
    @Query(value = "SELECT * FROM sub_cuenta_contable WHERE codigo = :codigo AND empresa = :empresa AND find_in_set(:ceco,ceco) AND tipo = :tipo ",nativeQuery = true)
   	public SubCuentaContableEntity obtenerSubCuentaC(@Param("codigo")String codigo,@Param("empresa")String empresa,@Param("ceco")  String ceco,@Param("tipo")  String tipo);
}
