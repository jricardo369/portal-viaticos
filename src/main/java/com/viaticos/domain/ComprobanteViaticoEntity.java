package com.viaticos.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "comprobante_viatico")
public class ComprobanteViaticoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_comprobante_viatico")
	private int id;

	@Column(name = "fecha_carga")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCarga;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sub_cuenta_contable")
	private SubCuentaContableEntity sub_cuenta_contable;

	@Column(name = "impuestos", precision = 14, scale = 2)
	private BigDecimal impuestos;

	@Column(name = "sub_total", precision = 14, scale = 2)
	private BigDecimal subTotal;

	@Column(name = "total", precision = 14, scale = 2)
	private BigDecimal total;

	@Column(name = "propina", precision = 14, scale = 2)
	private BigDecimal propina;

	@Column(name = "aprobacion_contador", columnDefinition = "TINYINT", length = 4)
	private boolean aprobacionContador;

	@Column(name = "aprobacion_gerente", columnDefinition = "TINYINT", length = 4)
	private boolean aprobacionGerente;

	@Column(name = "aprobacion_prestador", columnDefinition = "TINYINT", length = 4)
	private boolean aprobacionPrestador;

	@Column(name = "no_aplica", precision = 14, scale = 2)
	private BigDecimal noAplica;

	@Column(name = "aprobacion_no_aplica", columnDefinition = "TINYINT", length = 4)
	private boolean aprobacionNoAplica;
	
	@Column(name = "monto_aprobado", precision = 14, scale = 2)
	private BigDecimal montoAprobado;

	@Column(name = "estatus_comprobante", length = 45)
	private String estatusComprobante;

	@Column(name = "ruta_xml", length = 255)
	private String rutaXml;

	@Column(name = "ruta_pdf", length = 255)
	private String rutaPdf;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "tipo_gasto", length = 25)
	private String tipoGasto;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "numero_solicitud", nullable = false)
	private SolicitudViaticosEntity numero_solicitud;

	@OneToOne(mappedBy = "id_comprobante_viatico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private CfdiEntity cfdiEntity;

	public String getTipoGasto() {
		return tipoGasto;
	}

	public void setTipoGasto(String tipoGasto) {
		this.tipoGasto = tipoGasto;
	}

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

	public BigDecimal getPropina() {
		return propina;
	}

	public void setPropina(BigDecimal propina) {
		this.propina = propina;
	}

	public BigDecimal getImpuestos() {
		return impuestos;
	}

	public void setImpuestos(BigDecimal impuestos) {
		this.impuestos = impuestos;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga(Date fechaCarga) {
		this.fechaCarga = fechaCarga;
	}

	public SubCuentaContableEntity getSub_cuenta_contable() {
		return sub_cuenta_contable;
	}

	public void setSub_cuenta_contable(SubCuentaContableEntity sub_cuenta_contable) {
		this.sub_cuenta_contable = sub_cuenta_contable;
	}

	public boolean getAprobacionContador() {
		return aprobacionContador;
	}

	public void setAprobacionContador(boolean aprobacionContador) {
		this.aprobacionContador = aprobacionContador;
	}

	public boolean getAprobacionGerente() {
		return aprobacionGerente;
	}

	public void setAprobacionGerente(boolean aprobacionGerente) {
		this.aprobacionGerente = aprobacionGerente;
	}

	public boolean getAprobacionPrestador() {
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

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public SolicitudViaticosEntity getNumero_solicitud() {
		return numero_solicitud;
	}

	public void setNumero_solicitud(SolicitudViaticosEntity numero_solicitud) {
		this.numero_solicitud = numero_solicitud;
	}

	public CfdiEntity getCfdiEntity() {
		return cfdiEntity;
	}

	public void setCfdiEntity(CfdiEntity cfdiEntity) {
		this.cfdiEntity = cfdiEntity;
	}

	public BigDecimal getMontoAprobado() {
		return montoAprobado;
	}

	public void setMontoAprobado(BigDecimal montoAprobado) {
		this.montoAprobado = montoAprobado;
	}

	public ComprobanteViaticoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
