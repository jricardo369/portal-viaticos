package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.TareaProgramadaEntity;

@Repository
public interface TareasProgramadasJPA extends CrudRepository<TareaProgramadaEntity, Serializable>{

	public List<TareaProgramadaEntity> findAll();
	public TareaProgramadaEntity findById(int codigo);
	
}
