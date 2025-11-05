package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.EventoDeViaticoCase;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Service
public class EventoDeViaticoService implements EventoDeViaticoCase{

	@Autowired
	private EventoDeViaticoPort eventoPort;
	
	@Autowired
	private SolicitudesDeUsuarioPort solicitudPort;
	
	@Override
	public List<EventoViaticoEntity> obtenerEventosDeSolicitud(int numeroSolicitud) {
		
		SolicitudViaticosEntity solicitudEntity = new SolicitudViaticosEntity();
		solicitudEntity = solicitudPort.obtenerSolicitudJPA(numeroSolicitud);
		if(solicitudEntity == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		
		return eventoPort.obtenerBitacoraSolicitud(solicitudEntity);
	}

	@Override
	public void crearEventoDeSolicitud(EventoViaticoEntity evento) {
		eventoPort.crearEntradaEnBitacoraDeSolicitud(evento);
	}

}
