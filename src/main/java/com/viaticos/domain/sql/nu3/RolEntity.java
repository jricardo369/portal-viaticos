package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.Rol")
public class RolEntity {

	@Id
	@Column(name = "ID_Rol", insertable = false, updatable = false)
	private int idRol;

	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String descripcion;

	public int getIdRol() {
		return idRol;
	}

	public void setIdRol(int idRol) {
		this.idRol = idRol;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public RolEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
