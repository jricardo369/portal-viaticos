package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.EmpleadoEntity;

@Repository
public interface EmpleadoJPA extends JpaRepository<EmpleadoEntity, Serializable>{

	@Query(value = "SELECT [ID_Empleado]\r\n" + 
			"      ,[NombreCompleto]\r\n" + 
			"      ,[Estatus]\r\n" + 
			"	   ,[CorreoElectronico]\r\n" +
			"	   ,[UserName]\r\n" +
			"      ,[PasswordPortal]\r\n" + 
			"      ,[ID_Organizacion]\r\n" + 
			"      ,[ID_Grupo01]\r\n" + 
			"      ,[ID_Departamento]\r\n" + 
			"      ,[ID_Clasificacion]\r\n" + 
			"      ,[Foto]\r\n" +
			"	   ,[Texto08]\r\n" +
			"	   ,[ID_CentroCosto]\r\n" +
			"	   ,[ID_TablaAd04]" +
			"	   ,[RFC]"+
			"	   ,[Texto11]"
			+ " FROM [TempusNu3].[dbo].[Empleado] WHERE ID_Empleado = ?1", nativeQuery = true)
		//+ " FROM [TempusNu3Productividad].[dbo].[Empleado] WHERE ID_Empleado = ?1", nativeQuery = true)
	public EmpleadoEntity findByUserName(String userName);
	
	@Query(value = "SELECT [ID_Empleado]\r\n" + 
			"      ,[NombreCompleto]\r\n" + 
			"      ,[Estatus]\r\n" + 
			"	   ,[CorreoElectronico]\r\n" +
			"	   ,[UserName]\r\n" +
			"      ,[PasswordPortal]\r\n" + 
			"      ,[ID_Organizacion]\r\n" + 
			"      ,[ID_Grupo01]\r\n" + 
			"      ,[ID_Departamento]\r\n" + 
			"      ,[ID_Clasificacion]\r\n" + 
			"      ,[Foto]\r\n" +
			"	   ,[Texto08]\r\n" +
			"	   ,[ID_CentroCosto]\r\n" +
			"	   ,[ID_TablaAd04]" +
			"	   ,[RFC]" +
			"	   ,[Texto11]"
			+ " FROM [TempusNu3].[dbo].[Empleado] WHERE ID_Empleado IN (:empleados)", nativeQuery = true)
		//+ " FROM [TempusNu3Productividad].[dbo].[Empleado] WHERE ID_Empleado = ?1", nativeQuery = true)
	public List<EmpleadoEntity> obtenerEmpleados(@Param("empleados") List<String> empleados);
}