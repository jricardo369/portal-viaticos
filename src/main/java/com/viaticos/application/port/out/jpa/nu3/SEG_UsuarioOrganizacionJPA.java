package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.SEG_UsuarioOrganizacionEntity;

@Repository
public interface SEG_UsuarioOrganizacionJPA extends JpaRepository<SEG_UsuarioOrganizacionEntity, Serializable> {
	
	public List<SEG_UsuarioOrganizacionEntity> findByUdId(int udId);
	
	public List<SEG_UsuarioOrganizacionEntity> findByIdOrganizacion(String empresa);

}
