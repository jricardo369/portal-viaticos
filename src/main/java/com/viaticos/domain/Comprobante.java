package com.viaticos.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Comprobante {

	private int idComprobanteViatico;
	private Date fecha;
	private String rfc;
	private String numeroFactura;
	private BigDecimal iva;
	private BigDecimal isr;
	private BigDecimal ieps;
	private BigDecimal impuesto;
	private BigDecimal subTotal;
	private BigDecimal total;
	private String observaciones;
	private SubCuenta subCuenta;
	private BigDecimal noAplica;
	private boolean aprobacionNoAplica;
	private BigDecimal montoAprobado;

	private String subCuentaContable;
	// private String descripcionSubCuenta;

	private boolean aprobacionContador;
	private boolean aprobacionGerente;
	private boolean aprobacionPrestador;
	private String estatusComprobante;
	private BigDecimal propina;
	private String rutaXml;
	private String rutaPdf;
	private String numeroSolicitud;
	private Cfdi cfdi;

	public BigDecimal getNoAplica() {
		return noAplica;
	}

	public void setNoAplica(BigDecimal noAplica) {
		this.noAplica = noAplica;
	}

	public boolean isAprobacionNoAplica() {
		return aprobacionNoAplica;
	}

	public void setAprobacionNoAplica(boolean aprobacionNoAplica) {
		this.aprobacionNoAplica = aprobacionNoAplica;
	}

	public SubCuenta getSubCuenta() {
		return subCuenta;
	}

	public void setSubCuenta(SubCuenta subCuenta) {
		this.subCuenta = subCuenta;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public BigDecimal getIva() {
		return iva;
	}

	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public BigDecimal getIsr() {
		return isr;
	}

	public void setIsr(BigDecimal isr) {
		this.isr = isr;
	}

	public BigDecimal getIeps() {
		return ieps;
	}

	public void setIeps(BigDecimal ieps) {
		this.ieps = ieps;
	}

	/*
	 * public String getDescripcionSubCuenta() { return descripcionSubCuenta; }
	 * 
	 * public void setDescripcionSubCuenta(String descripcionSubCuenta) {
	 * this.descripcionSubCuenta = descripcionSubCuenta; }
	 */
	public BigDecimal getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(BigDecimal impuesto) {
		this.impuesto = impuesto;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getPropina() {
		return propina;
	}

	public void setPropina(BigDecimal propina) {
		this.propina = propina;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getSubCuentaContable() {
		return subCuentaContable;
	}

	public void setSubCuentaContable(String subCuentaContable) {
		this.subCuentaContable = subCuentaContable;
	}

	public boolean isAprobacionContador() {
		return aprobacionContador;
	}

	public void setAprobacionContador(boolean aprobacionContador) {
		this.aprobacionContador = aprobacionContador;
	}

	public boolean isAprobacionGerente() {
		return aprobacionGerente;
	}

	public void setAprobacionGerente(boolean aprobacionGerente) {
		this.aprobacionGerente = aprobacionGerente;
	}

	public boolean isAprobacionPrestador() {
		return aprobacionPrestador;
	}

	public void setAprobacionPrestador(boolean aprobacionPrestador) {
		this.aprobacionPrestador = aprobacionPrestador;
	}

	public String getEstatusComprobante() {
		return estatusComprobante;
	}

	public void setEstatusComprobante(String estatusComprobante) {
		this.estatusComprobante = estatusComprobante;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getRutaXml() {
		return rutaXml;
	}

	public void setRutaXml(String rutaXml) {
		this.rutaXml = rutaXml;
	}

	public String getRutaPdf() {
		return rutaPdf;
	}

	public void setRutaPdf(String rutaPdf) {
		this.rutaPdf = rutaPdf;
	}

	public Cfdi getCfdi() {
		return cfdi;
	}

	public void setCfdi(Cfdi cfdi) {
		this.cfdi = cfdi;
	}

	public int getIdComprobanteViatico() {
		return idComprobanteViatico;
	}

	public void setIdComprobanteViatico(int idComprobanteViatico) {
		this.idComprobanteViatico = idComprobanteViatico;
	}

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}

	public BigDecimal getMontoAprobado() {
		return montoAprobado;
	}

	public void setMontoAprobado(BigDecimal montoAprobado) {
		this.montoAprobado = montoAprobado;
	}

	public Comprobante() {
		super();
		// TODO Auto-generated constructor stub
	}

}
