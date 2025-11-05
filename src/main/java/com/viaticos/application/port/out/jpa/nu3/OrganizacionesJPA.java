package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.OrgEntity;

@Repository
public interface OrganizacionesJPA extends JpaRepository<OrgEntity, Serializable> {

	public List<OrgEntity> findByIdOrganizacion(String idOrganizacion);

	@Query(value = "SELECT ID_Organizacion,nombre,Descripcion, RFC FROM Organizacion WHERE RFC <> ''", nativeQuery = true)
	public List<OrgEntity> obtenerOrganizaciones();
	
	@Query(value = "SELECT  ROW_NUMBER() OVER(ORDER BY RFC ASC) as ID_Organizacion,'' as Descripcion,Nombre,RFC FROM Organizacion WHERE RFC <> '' GROUP BY Nombre,RFC order by RFC ", nativeQuery = true)
	public List<OrgEntity> obtenerOrganizacionesConRFC();

}
