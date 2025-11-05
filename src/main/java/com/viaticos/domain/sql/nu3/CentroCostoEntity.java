package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.CentroCosto")
public class CentroCostoEntity {

	@Id
	@Column(name = "ID_CentroCosto", insertable = false, updatable = false)
	private String id;

	@Column(name = "Activo", insertable = false, updatable = false)
	private boolean activo;

	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String descripcion;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public CentroCostoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
