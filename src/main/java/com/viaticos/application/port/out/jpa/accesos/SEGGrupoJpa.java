package com.viaticos.application.port.out.jpa.accesos;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viaticos.domain.sql.accesos.SEG_GruposEntity;

public interface SEGGrupoJpa extends JpaRepository<SEG_GruposEntity, Serializable>{
	
	@Query(value = "SELECT [GRP_Id], [GRP_Nombre] FROM [tempusAccesos].[dbo].[SEG_Grupos] WHERE GRP_Id IN (:id)", nativeQuery = true)
	List<SEG_GruposEntity> obtenerListaGrupos(@Param("id") List<Integer> id);
	
	SEG_GruposEntity findByGrpNombre(String nombre);

}
