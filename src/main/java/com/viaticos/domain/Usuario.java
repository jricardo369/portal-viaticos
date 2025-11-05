package com.viaticos.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viaticos.domain.sql.nu3.Departamento;
import com.viaticos.domain.sql.nu3.Grupo01Model;
import com.viaticos.domain.sql.nu3.OrganizacionesModel;
import com.viaticos.domain.sql.nu3.RolModel;

public class Usuario {

	private String usuario;
	private String nombre;
	private List<OrganizacionesModel> organizaciones;
	private List<Grupo01Model> grupo01;
	private List<Departamento> departamentos;
	private String ceco;
	private String cecoDesc;
	private String nivel;
	private String correoElectronico;
	private String cuentaContable;

	@JsonIgnore
	private String password;
	private List<RolModel> rol;
	@SuppressWarnings("unused")
	private String idDepartamento;
	@SuppressWarnings("unused")
	private String departamento;
	private String foto;
	private String rfc;
	private String proyecto;
	private boolean puedeAprobarSolsDirectores;

	public String getCecoDesc() {
		return cecoDesc;
	}

	public void setCecoDesc(String cecoDesc) {
		this.cecoDesc = cecoDesc;
	}

	public String getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public List<Grupo01Model> getGrupo01() {
		return grupo01;
	}

	public void setGrupo01(List<Grupo01Model> grupo01) {
		this.grupo01 = grupo01;
	}

	/*
	 * public String getIdDepartamento() { return idDepartamento; }
	 * 
	 * public void setIdDepartamento(String idDepartamento) { this.idDepartamento =
	 * idDepartamento; }
	 * 
	 * public String getDepartamento() { return departamento; }
	 * 
	 * public void setDepartamento(String departamento) { this.departamento =
	 * departamento; }
	 */
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<OrganizacionesModel> getOrganizaciones() {
		return organizaciones;
	}

	public void setOrganizaciones(List<OrganizacionesModel> organizaciones) {
		this.organizaciones = organizaciones;
	}

	public String getCeco() {
		return ceco;
	}

	public void setCeco(String ceco) {
		this.ceco = ceco;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<RolModel> getRol() {
		return rol;
	}

	public void setRol(List<RolModel> rol) {
		this.rol = rol;
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

	public String getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(String idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public boolean isPuedeAprobarSolsDirectores() {
		return puedeAprobarSolsDirectores;
	}

	public void setPuedeAprobarSolsDirectores(boolean puedeAprobarSolsDirectores) {
		this.puedeAprobarSolsDirectores = puedeAprobarSolsDirectores;
	}

}
