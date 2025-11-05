package com.viaticos.domain.sql.accesos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.SEG_Grupos")
public class SEG_GruposEntity {
	
	@Id
	@Column(name = "GRP_Id", insertable = false, updatable = false)
	private int grpId;
	
	@Column(name = "GRP_Nombre", insertable = false, updatable = false)
	private String grpNombre;

	public int getGrpId() {
		return grpId;
	}

	public void setGrpId(int grpId) {
		this.grpId = grpId;
	}

	public String getGrpNombre() {
		return grpNombre;
	}

	public void setGrpNombre(String grpNombre) {
		this.grpNombre = grpNombre;
	}

	public SEG_GruposEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
