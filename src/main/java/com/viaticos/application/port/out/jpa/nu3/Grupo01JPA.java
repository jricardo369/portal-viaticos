package com.viaticos.application.port.out.jpa.nu3;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.sql.nu3.Grupo01Entity;

@Repository
public interface Grupo01JPA extends JpaRepository<Grupo01Entity, Serializable> {
	
	public List<Grupo01Entity> findByIdGrupo01AndActivo(String idGrupo, boolean activo);

}
