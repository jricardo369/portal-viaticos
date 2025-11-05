package com.viaticos.application.port.in;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.viaticos.domain.Comprobante;
import com.viaticos.domain.Solicitud;

public interface ViaticosDeUsuarioUseCase {
	
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatus(String usuario,String empleado, String estatus);
	public void solicitarViaticosParaUsuario(int solicitud, int estatusSolicitud, String usuarioTempus);
	public Solicitud obtenerSolicitud(String numeroSolicitud);
	public void cargaDeComprobante(String numeroSolicitud, Comprobante comprobante, byte[] xml, byte[] pdf);
	public Comprobante obtenerDeComprobante(int idComprobante);
	public void modificarDeComprobante(int idComprobante, Comprobante comprobante, MultipartFile xml, MultipartFile pdf);
	public void eliminarDeComprobante(int idComprobante);
	public void solicitarAprobacionDeComprobacion(Solicitud solicitud);
	public void recalculoISH(int numeroSolicitud);
	public void modificarComprobante(int idComprobante, Comprobante comprobante, String aprobacionAplica, String usuario);
	public void modificarMontoAprobado(int idComprobante, BigDecimal montoADescontar, String usuario);
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatusJPA( String empleado, String estatusQ);
	public Solicitud obtenerSolicitudJPA(int numeroSolicitud);
	public Solicitud crearSolicitud(String empleado, Solicitud solicitud);
	public void cargaDeComprobanteJPA(String numeroSolicitud, Comprobante comprobante, MultipartFile xml, MultipartFile pdf);
	public void recalculoNoAplica(int numeroSolicitud);
	public void validaCfdi(MultipartFile xml);
	public void eliminarSolicitud(int solicitud);
	public void editarSolicitud(Solicitud solicitud);
	public void enviaEstatusAprobacionContador(int solicitud, int estatus);
	public void solicitaAprobacionDeComprobante(int comprobante, String estatus);
	public List<Solicitud> obtenerSolicitudesReporte(String estatus,String empresas ,String fechaInicio,String fechaFin,String evento,int numeroSolicitud);
	public List<Solicitud> obtenerSolicitudesReporteDirector(String estatus,String empresas ,String fechaInicio,String fechaFin);
	public List<Solicitud> obtenerSolicitudesPorEmpresasEstatus(String empresas, String estatus, String usuario);
	public void altaDispercionParaPruebas(int idSolicitud);
	public void actualizaEstatusSolicitud(int numeroSolicitud, int estatus);
	
	
}
