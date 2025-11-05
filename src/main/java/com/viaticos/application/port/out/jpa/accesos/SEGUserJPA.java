package com.viaticos.application.port.out.jpa.accesos;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.accesos.UsuarioEntity;

@Repository
public interface SEGUserJPA extends JpaRepository<UsuarioEntity, Serializable> {

	public UsuarioEntity findByusNCortoAndUsActivo(String usNCorto, boolean activo);
	
	@Query(value = "SELECT [US_Id], [US_NCorto], [US_Nombres], [US_Psw], [US_EsAdmin], [US_Email], [US_Activo] FROM [tempusAccesos].[dbo].[SEG_Usuario] WHERE US_Id IN (:id) AND US_Email != '' AND US_Activo = 1", nativeQuery = true)
	public List<UsuarioEntity> encuentraUsuariosPorId(@Param("id") List<Integer> id);
}
