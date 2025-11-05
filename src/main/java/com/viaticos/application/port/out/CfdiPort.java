package com.viaticos.application.port.out;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.viaticos.domain.Cfdi;
import com.viaticos.domain.CfdiEntity;

public interface CfdiPort {
	
	public Cfdi validarCfdi(byte[] archivo);
	public boolean existeCfdi(String uuid);
	
	public boolean existeCfdiJpa(String uuid);
	public void guardaCfdi(CfdiEntity cfdi);
	public BigDecimal totalPorTipoSubCuentaYFecha(String codigo, int numeroSolicitud,Date fecha);
	public BigDecimal totalPorNombreSubCuentaYFecha(String nombre, int numeroSolicitud,Date fecha);
	public BigDecimal totalPorTipoSubCuenta(String codigo,int numeroSolicitud);
	public BigDecimal totalPorNombre(String nombre, int numeroSolicitud);
	public BigDecimal totalPorTipoSubCuentaNotIn(String codigo,int numeroSolicitud);
	public BigDecimal totalPropina(int numeroSolicitud);
	public BigDecimal totalComprobado(int numeroSolicitud);
	public BigDecimal totalComprobadoSinPropina(int numeroSolicitud);
	public BigDecimal totalComprobadoNoDeducible(int numeroSolicitud);
	public BigDecimal totalComprobadoDeducible(int numeroSolicitud);
	public List<String> totalesDeFecha(Date fecha,int numeroSolicitud);
	public BigDecimal totalNoDeduciblePorAnio(String ejercicio,String usuario);
	public BigDecimal totalNoAplica(int numeroSolicitud,boolean aplica);
	public BigDecimal totalNoAplicaPorComprobante(int numeroSolicitud,boolean aplica,int idComprobante);
	public void actualizarISH(int idCfdi,BigDecimal ish);
	public CfdiEntity obtenerCfdiPorUuid(String uuid);
	public BigDecimal totalReintegroPorSolicitud(int numeroSolicitud);
	
}
