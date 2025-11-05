package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viaticos.domain.sql.nu3.SEG_UsuarioRolEntity;

public interface SEG_UsuarioRol extends JpaRepository<SEG_UsuarioRolEntity, Serializable> {

	@Query(value = "SELECT [SUR_ID], [US_ID], [GRP_ID], [SUR_Miembro] FROM [TempusNu3].[dbo].[SEG_UsuarioRol] WHERE US_ID = ?1 AND SUR_Miembro = 1", nativeQuery = true)
	public List<SEG_UsuarioRolEntity> encuentraListaRol(int id);
	
	@Query(value = "SELECT [SUR_ID], [US_ID], [GRP_ID], [SUR_Miembro] FROM [TempusNu3].[dbo].[SEG_UsuarioRol] WHERE US_ID IN (:usuarios) AND GRP_ID =:rolID AND SUR_Miembro = 1", nativeQuery = true)
	public List<SEG_UsuarioRolEntity> encuentraRolesPorUsuarioYID(@Param("usuarios") List<Integer> usuarios, @Param("rolID") int rol);
	
//	@Query(value = "SELECT [SUR_ID], [US_ID], [GRP_ID], [SUR_Miembro] FROM [TempusNu3Productividad].[dbo].[SEG_UsuarioRol] WHERE US_ID = ?1", nativeQuery = true)
//	public List<SEG_UsuarioRolEntity> encuentraListaRol(int id);
//	
//	@Query(value = "SELECT [SUR_ID], [US_ID], [GRP_ID], [SUR_Miembro] FROM [TempusNu3Productividad].[dbo].[SEG_UsuarioRol] WHERE US_ID IN (:usuarios) AND GRP_ID =:rolID", nativeQuery = true)
//	public List<SEG_UsuarioRolEntity> encuentraRolesPorUsuarioYID(@Param("usuarios") List<Integer> usuarios, @Param("rolID") int rol);
}
