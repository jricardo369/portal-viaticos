package com.viaticos.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "empresa")
public class EmpresaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_empresa")
	private int id;
	
	@Column(name = "empresa", length = 120)
	private String empresa;
	
	@Column(name = "codigo_empresa", length = 200)
	private String codigo_empresa;
	
	@Column(name = "sistema", length = 150)
	private String sistema;
	
	@Column(name = "base_de_datos", length = 45)
	private String baseDeDatos;
	
	@Column(name = "datos_conexion", length = 500)
	private String datosConexion;
	
	@Column(name = "division", length = 4)
	private String division;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	public String getCodigo_empresa() {
		return codigo_empresa;
	}

	public void setCodigo_empresa(String codigo_empresa) {
		this.codigo_empresa = codigo_empresa;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getBaseDeDatos() {
		return baseDeDatos;
	}

	public void setBaseDeDatos(String baseDeDatos) {
		this.baseDeDatos = baseDeDatos;
	}

	public String getDatosConexion() {
		return datosConexion;
	}

	public void setDatosConexion(String datosConexion) {
		this.datosConexion = datosConexion;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
	
}
