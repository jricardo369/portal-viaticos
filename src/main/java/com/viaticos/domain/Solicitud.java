package com.viaticos.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viaticos.domain.sql.nu3.Departamento;
import com.viaticos.domain.sql.nu3.Grupo01Model;

public class Solicitud {

	private String numeroSolicitud;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es-MX", timezone = "America/Mexico_City")
	private Date fechaInicio;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es-MX", timezone = "America/Mexico_City")
	private Date fechaFin;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "es-MX", timezone = "America/Mexico_City")
	private Date fechaCreacion;
	private String motivo;
	private String concepto;
	private BigDecimal totalAnticipo;
	private BigDecimal totalComprobado;
	private String estatus;
	private String estatusDescripcion;
	private String cuentaContable;
	private String nivel;

	@JsonIgnore
	private Usuario usuarioObj;
	private String observaciones;
	private String empresa;
	
	@JsonProperty("empresaDescripcion")
	private String empresaDescr;
	private String ceco;
	
	@JsonProperty("cecoDescripcion")
	private String cecoDesc;
	
	private String usuario;
	private String nombreCompletoUsuario;
	private List<Comprobante> comprobantes;
	private Grupo01Model grupo;
	private Departamento departamento;
	private String rfc;
	private String proyecto;

	public String getEmpresaDescr() {
		return empresaDescr;
	}

	public void setEmpresaDescr(String empresaDescr) {
		this.empresaDescr = empresaDescr;
	}

	public String getCecoDesc() {
		return cecoDesc;
	}

	public void setCecoDesc(String cecoDesc) {
		this.cecoDesc = cecoDesc;
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

	public Grupo01Model getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo01Model grupo) {
		this.grupo = grupo;
	}

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
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

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public BigDecimal getTotalAnticipo() {
		return totalAnticipo;
	}

	public void setTotalAnticipo(BigDecimal totalAnticipo) {
		this.totalAnticipo = totalAnticipo;
	}

	public BigDecimal getTotalComprobado() {
		return totalComprobado;
	}

	public void setTotalComprobado(BigDecimal totalComprobado) {
		this.totalComprobado = totalComprobado;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getEstatusDescripcion() {
		return estatusDescripcion;
	}

	public void setEstatusDescripcion(String estatusDescripcion) {
		this.estatusDescripcion = estatusDescripcion;
	}

	public Usuario getUsuarioObj() {
		return usuarioObj;
	}

	public void setUsuarioObj(Usuario usuarioObj) {
		this.usuarioObj = usuarioObj;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<Comprobante> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(List<Comprobante> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
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

}
