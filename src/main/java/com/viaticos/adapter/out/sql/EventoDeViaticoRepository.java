package com.viaticos.adapter.out.sql;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.jpa.mysql.EventosDeViaticosJPA;
import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@Service
public class EventoDeViaticoRepository implements EventoDeViaticoPort {

	@Autowired
	private EventosDeViaticosJPA eventoJPA;
	
	@Override
	public void crearEntradaEnBitacoraDeSolicitud(EventoViaticoEntity evento) {
		eventoJPA.save(evento);
	}

	@Override
	public List<EventoViaticoEntity> obtenerBitacoraSolicitud(SolicitudViaticosEntity numeroSolicitud) {
		return eventoJPA.encuentraPorNumeroSolicitud(numeroSolicitud);
	}
	
	@Override
	public boolean tieneEventoLaSolicitud(int numeroSolicitud,String evento) {
		boolean tieneEvento = false;
		int tieneEventoInt = eventoJPA.tieneEventoLaSolicitud(numeroSolicitud, evento);
		if(tieneEventoInt != 0) {
			tieneEvento = true;
		}
		return tieneEvento;
	}
	
	@Override
	public void ingresarEventoDeSolicitud(String evento,String texto,SolicitudViaticosEntity solicitudEntity, String nombreUsuario) {
		EventoViaticoEntity eventoViatico = new EventoViaticoEntity();
		eventoViatico.setEvento(evento);
		eventoViatico.setNumero_solicitud(solicitudEntity);
		eventoViatico.setTexto(texto);
		eventoViatico.setUsuario(nombreUsuario.toUpperCase());
		eventoViatico.setFecha(new Date());
		System.out.println("Se agregara evento " + evento + " a solicitud " + solicitudEntity.getId());
	    crearEntradaEnBitacoraDeSolicitud(eventoViatico);
	}

}
