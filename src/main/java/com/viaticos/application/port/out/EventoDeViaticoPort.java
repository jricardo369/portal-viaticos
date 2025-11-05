package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

public interface EventoDeViaticoPort {

	public void crearEntradaEnBitacoraDeSolicitud(EventoViaticoEntity evento);
	public List<EventoViaticoEntity> obtenerBitacoraSolicitud(SolicitudViaticosEntity numeroSolicitud);
	public void ingresarEventoDeSolicitud(String evento,String texto,SolicitudViaticosEntity solicitud, String nombreUsuario);
	public boolean tieneEventoLaSolicitud(int numeroSolicitud,String evento);
}
