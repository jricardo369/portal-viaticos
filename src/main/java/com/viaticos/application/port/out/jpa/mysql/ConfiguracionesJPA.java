package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.ConfiguracionEntity;

@Repository
public interface ConfiguracionesJPA extends CrudRepository<ConfiguracionEntity, Serializable>{

	public List<ConfiguracionEntity> findAll();
	public ConfiguracionEntity findById(int codigo);
	
}
