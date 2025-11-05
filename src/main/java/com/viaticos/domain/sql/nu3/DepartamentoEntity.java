package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.Departamento")
public class DepartamentoEntity {

	@Id
	@Column(name = "ID_Departamento", insertable = false, updatable = false)
	private String idDepartamento;

	@Column(name = "Activo", insertable = false, updatable = false)
	private boolean activo;

	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String descripcion;

	public String getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(String idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public DepartamentoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
