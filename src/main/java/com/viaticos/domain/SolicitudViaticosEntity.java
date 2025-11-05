package com.viaticos.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "solicitud_de_viaticos")
public class SolicitudViaticosEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "numero_solicitud")
	private int numeroSolicitud;

	@Column(name = "motivo", length = 150)
	private String motivo;

	@Column(name = "fecha_inicio")
	@Temporal(TemporalType.DATE)
	private Date fechaInicio;

	@Column(name = "fecha_fin")
	@Temporal(TemporalType.DATE)
	private Date fechaFin;

	@Column(name = "fecha_creacion")
	@Temporal(TemporalType.DATE)
	private Date fechaCreacion;

	@Column(name = "anticipo", precision = 14, scale = 2)
	private BigDecimal anticipo;

	@Column(name = "usuario", length = 45)
	private String usuario;

	@Column(name = "nombre_usuario", length = 120)
	private String nombreCompletoUsuario;

	@Column(name = "empresa", length = 45)
	private String empresa;

	@Column(name = "ceco", length = 45)
	private String ceco;

	@Column(name = "concepto", length = 45)
	private String concepto;

	@Column(name = "observaciones", length = 200)
	private String observaciones;

	@Column(name = "cuenta_contable", length = 50)
	private String cuentaContable;

	@Column(name = "nivel", length = 10)
	private String nivel;

	@Column(name = "ceco_descripcion", length = 150)
	private String cecoDescr;

	@Column(name = "empresa_descripcion", length = 150)
	private String empresaDescr;

	@JoinColumn(name = "estatus", nullable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private EstatusSolicitudEntity estatus;

	@Column(name = "rfc", length = 13)
	private String rfc;
	
	@Column(name = "proyecto", length = 10)
	private String proyecto;

	@OneToMany(mappedBy = "numero_solicitud", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private List<ComprobanteViaticoEntity> comprobanteViaticosEntity;

	@OneToMany(mappedBy = "numero_solicitud", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<EventoViaticoEntity> eventoViaticoEntity;

	public String getEmpresaDescr() {
		return empresaDescr;
	}

	public void setEmpresaDescr(String empresaDescr) {
		this.empresaDescr = empresaDescr;
	}

	public String getCecoDescr() {
		return cecoDescr;
	}

	public void setCecoDescr(String cecoDescr) {
		this.cecoDescr = cecoDescr;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getNombreCompletoUsuario() {
		return nombreCompletoUsuario;
	}

	public void setNombreCompletoUsuario(String nombreCompletoUsuario) {
		this.nombreCompletoUsuario = nombreCompletoUsuario;
	}

	public int getId() {
		return numeroSolicitud;
	}

	public void setId(int id) {
		this.numeroSolicitud = id;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public BigDecimal getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(BigDecimal anticipo) {
		this.anticipo = anticipo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getCeco() {
		return ceco;
	}

	public void setCeco(String ceco) {
		this.ceco = ceco;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public EstatusSolicitudEntity getEstatus() {
		return estatus;
	}

	public void setEstatus(EstatusSolicitudEntity estatus) {
		this.estatus = estatus;
	}

	public List<ComprobanteViaticoEntity> getComprobanteViaticosEntity() {
		return comprobanteViaticosEntity;
	}

	public void setComprobanteViaticosEntity(List<ComprobanteViaticoEntity> comprobanteViaticosEntity) {
		this.comprobanteViaticosEntity = comprobanteViaticosEntity;
	}

	public List<EventoViaticoEntity> getEventoViaticoEntity() {
		return eventoViaticoEntity;
	}

	public void setEventoViaticoEntity(List<EventoViaticoEntity> eventoViaticoEntity) {
		this.eventoViaticoEntity = eventoViaticoEntity;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	
	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public SolicitudViaticosEntity() {
		super();
	}

}
