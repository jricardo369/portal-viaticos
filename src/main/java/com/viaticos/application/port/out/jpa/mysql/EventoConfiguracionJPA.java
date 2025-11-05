package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.EventoConfiguracionEntity;

@Repository
public interface EventoConfiguracionJPA extends JpaRepository<EventoConfiguracionEntity, Serializable> {

	public List<EventoConfiguracionEntity> findAll();
	public EventoConfiguracionEntity findById(int id);
	public EventoConfiguracionEntity findByEvento(String evento);

}
