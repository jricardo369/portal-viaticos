package com.viaticos.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Cfdi {

	private int id;
	private String serie;
	private String version;
	private String folio;
	private String aprovnum;
	private String aprovyear;
	private Date fecha;
	private BigDecimal total;
	private BigDecimal subtotal;
	private String moneda;
	private String noCertificado;
	private String sello;
	private String certificado;
	private String uuid;
	private String nombreEmisor;
	private String rfcReceptor;
	private String rfcEmisor;
	private String mensaje;
	private boolean valida;
	private boolean isCFDI;
	private String tipoComp;
	private double tasaiva;
	private BigDecimal totalImpuestosRetenidos;
	private BigDecimal ivaRetenido;
	private BigDecimal isrRetenido;
	private BigDecimal iepsRetenido;
	private BigDecimal totalImpuestosTrasladados;
	private BigDecimal ivaTrasladado;
	private BigDecimal iepsTrasladado;
	private BigDecimal totalDeRetencionesLocales;
	private BigDecimal totalDeTrasladosLocales;
	private BigDecimal descuento;
	private BigDecimal ishTrasladado;
	private String idComprobante;
	private String metodoPago;
	private String formaPago;
	private List<Impuesto> impuestos;
	private boolean tieneIVA0;
	private BigDecimal iva16;
	private BigDecimal tasaIva16;
	
	public String getIdComprobante() {
		return idComprobante;
	}

	public void setIdComprobante(String idComprobante) {
		this.idComprobante = idComprobante;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getAprovnum() {
		return aprovnum;
	}

	public void setAprovnum(String aprovnum) {
		this.aprovnum = aprovnum;
	}

	public String getAprovyear() {
		return aprovyear;
	}

	public void setAprovyear(String aprovyear) {
		this.aprovyear = aprovyear;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getNoCertificado() {
		return noCertificado;
	}

	public void setNoCertificado(String noCertificado) {
		this.noCertificado = noCertificado;
	}

	public String getSello() {
		return sello;
	}

	public void setSello(String sello) {
		this.sello = sello;
	}

	public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRfcReceptor() {
		return rfcReceptor;
	}

	public void setRfcReceptor(String rfcReceptor) {
		this.rfcReceptor = rfcReceptor;
	}

	public String getRfcEmisor() {
		return rfcEmisor;
	}

	public void setRfcEmisor(String rfcEmisor) {
		this.rfcEmisor = rfcEmisor;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public boolean isValida() {
		return valida;
	}

	public void setValida(boolean valida) {
		this.valida = valida;
	}

	public boolean isCFDI() {
		return isCFDI;
	}

	public void setCFDI(boolean isCFDI) {
		this.isCFDI = isCFDI;
	}

	public String getTipoComp() {
		return tipoComp;
	}

	public void setTipoComp(String tipoComp) {
		this.tipoComp = tipoComp;
	}

	public double getTasaiva() {
		return tasaiva;
	}

	public void setTasaiva(double tasaiva) {
		this.tasaiva = tasaiva;
	}

	public BigDecimal getTotalImpuestosRetenidos() {
		return totalImpuestosRetenidos;
	}

	public void setTotalImpuestosRetenidos(BigDecimal totalImpuestosRetenidos) {
		this.totalImpuestosRetenidos = totalImpuestosRetenidos;
	}

	public BigDecimal getIvaRetenido() {
		return ivaRetenido;
	}

	public void setIvaRetenido(BigDecimal ivaRetenido) {
		this.ivaRetenido = ivaRetenido;
	}

	public BigDecimal getIsrRetenido() {
		return isrRetenido;
	}

	public void setIsrRetenido(BigDecimal isrRetenido) {
		this.isrRetenido = isrRetenido;
	}

	public BigDecimal getIepsRetenido() {
		return iepsRetenido;
	}

	public void setIepsRetenido(BigDecimal iepsRetenido) {
		this.iepsRetenido = iepsRetenido;
	}

	public BigDecimal getTotalImpuestosTrasladados() {
		return totalImpuestosTrasladados;
	}

	public void setTotalImpuestosTrasladados(BigDecimal totalImpuestosTrasladados) {
		this.totalImpuestosTrasladados = totalImpuestosTrasladados;
	}

	public BigDecimal getIvaTrasladado() {
		return ivaTrasladado;
	}

	public void setIvaTrasladado(BigDecimal ivaTrasladado) {
		this.ivaTrasladado = ivaTrasladado;
	}

	public BigDecimal getIepsTrasladado() {
		return iepsTrasladado;
	}

	public void setIepsTrasladado(BigDecimal iepsTrasladado) {
		this.iepsTrasladado = iepsTrasladado;
	}

	public BigDecimal getTotalDeRetencionesLocales() {
		return totalDeRetencionesLocales;
	}

	public void setTotalDeRetencionesLocales(BigDecimal totalDeRetencionesLocales) {
		this.totalDeRetencionesLocales = totalDeRetencionesLocales;
	}

	public BigDecimal getTotalDeTrasladosLocales() {
		return totalDeTrasladosLocales;
	}

	public void setTotalDeTrasladosLocales(BigDecimal totalDeTrasladosLocales) {
		this.totalDeTrasladosLocales = totalDeTrasladosLocales;
	}

	public BigDecimal getDescuento() {
		return descuento;
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}

	public BigDecimal getIshTrasladado() {
		return ishTrasladado;
	}

	public void setIshTrasladado(BigDecimal ishTrasladado) {
		this.ishTrasladado = ishTrasladado;
	}

	public String getNombreEmisor() {
		return nombreEmisor;
	}

	public void setNombreEmisor(String nombreEmisor) {
		this.nombreEmisor = nombreEmisor;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public List<Impuesto> getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(List<Impuesto> impuestos) {
		this.impuestos = impuestos;
	}

	public boolean isTieneIVA0() {
		return tieneIVA0;
	}

	public void setTieneIVA0(boolean tieneIVA0) {
		this.tieneIVA0 = tieneIVA0;
	}

	public BigDecimal getIva16() {
		return iva16;
	}

	public void setIva16(BigDecimal iva16) {
		this.iva16 = iva16;
	}

	public BigDecimal getTasaIva16() {
		return tasaIva16;
	}

	public void setTasaIva16(BigDecimal tasaIva16) {
		this.tasaIva16 = tasaIva16;
	}

	
}
