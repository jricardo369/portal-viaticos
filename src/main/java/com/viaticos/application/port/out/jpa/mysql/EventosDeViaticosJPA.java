package com.viaticos.application.port.out.jpa.mysql;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Repository
public interface EventosDeViaticosJPA extends JpaRepository<EventoViaticoEntity, Serializable> {

	@Query(value = "SELECT id_evento,fecha,evento,texto,usuario,numero_solicitud from evento_de_viatico e where numero_solicitud = ?1", nativeQuery = true)
	public List<EventoViaticoEntity> encuentraPorNumeroSolicitud(SolicitudViaticosEntity numeroSolicitud);
	
	@Query(value = "SELECT count(*) from evento_de_viatico  e where numero_solicitud = ?1 AND evento = ?2"
			+ "", nativeQuery = true)
	public int tieneEventoLaSolicitud(int numeroSolicitud,String evento);

}
