package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.EventoViaticoEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.SolicitudViaticosEntity;

public interface SolicitudesDeUsuarioPort {
	
	public String crearSolicitudDeUsuario(Solicitud solicitud);
	public List<Solicitud> encontrarSolicitudesDeEmpladoPorEstatus(String empleado,String estatus);
	public List<Solicitud> encontrarSolicitudesDePorEstatus(String estatus);
	public Solicitud obtenerSolicitud(String numeroSolicitud);
	public void actualizarAutorizacionCambioEstatus(String numeroSolicitud, String estatus);

	public List<SolicitudViaticosEntity> encontrarSolicitudesDeEmpladoPorEstatusJPA(String empleado, List<Integer> estatus);
	public SolicitudViaticosEntity obtenerSolicitudJPA(int numeroSolicitud);
	public SolicitudViaticosEntity crearSolicitud(String solicitante, SolicitudViaticosEntity solicitud);
	public void crearEventoSolicitud(EventoViaticoEntity eventoViatico);
	public void eliminarSolicitud(SolicitudViaticosEntity solicitud);
	public void editarSolicitud(SolicitudViaticosEntity solicitud);
	public void enviaPeticionAceptacion(int solicitud, EstatusSolicitudEntity estatus);
	public void enviaPeticionComprobante(int comprobante, String estatusDescripcion);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEstatus(List<EstatusSolicitudEntity> estatus);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEvento(String evento);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYEstatus(String evento,String estatus);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistema(String evento,String sistema);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistemaYEstatus(String evento, String sistema,String estatus);
	public List<Solicitud> obtenerSolicitudesReporte(String estatus,String empresas ,String fechaInicio,String fechaFin,int numeroSolicitud);
	public List<Solicitud> obtenerSolicitudesReporteDirector(String estatus,String empresas ,String fechaInicio,String fechaFin);
	public List<SolicitudViaticosEntity> obtenerSolicitudesPorEmpresasYEstatus(List<Integer> estatus, List<String> empresas, String rol);
	public List<SolicitudViaticosEntity> obtenerSolicitudesPorEmpresasYEstatusDirector(List<Integer> estatus, List<String> empresas);
	public List<Solicitud> obtenerPorEventoYFechas(String evento,String fechaInicio,String fechaFin);
	public SolicitudViaticosEntity obtenerEventoPorSolicitud(String evento,int sol);
	public List<Solicitud> obtenerSoloSolicitudPorNumSolicitud(int numeroSolicitud);
	public List<SolicitudViaticosEntity> obtenerSolicitudesFueraRango(int dias);
	public List<SolicitudViaticosEntity> encontrarSolicitudesPorEventoYSistemaYEstatusNotIn(String evento, String sistema,String estatus);
}
