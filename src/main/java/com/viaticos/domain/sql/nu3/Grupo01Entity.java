package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.Grupo01")
public class Grupo01Entity {

	@Id
	@Column(name = "ID_Grupo01", insertable = false, updatable = false)
	private String idGrupo01;

	@Column(name = "Activo", insertable = false, updatable = false)
	private boolean activo;

	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String descripcion;

	public String getIdGrupo01() {
		return idGrupo01;
	}

	public void setIdGrupo01(String idGrupo01) {
		this.idGrupo01 = idGrupo01;
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

	public Grupo01Entity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
