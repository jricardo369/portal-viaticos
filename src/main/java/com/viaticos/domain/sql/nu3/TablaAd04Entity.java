package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.TablaAd04")
public class TablaAd04Entity {
	
	@Id
	@Column(name = "ID_TablaAd04", insertable = false, updatable = false)
	private String id;
	
	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String descripcion;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public TablaAd04Entity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
