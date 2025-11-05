package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.NivelTopeUsuarioEntity;

@Repository
public interface NivelesJPA extends CrudRepository<NivelTopeUsuarioEntity, Serializable>{

	public List<NivelTopeUsuarioEntity> findAll();
	public NivelTopeUsuarioEntity findById(int id);
	public NivelTopeUsuarioEntity findByNivel(int nivel);
	
}
