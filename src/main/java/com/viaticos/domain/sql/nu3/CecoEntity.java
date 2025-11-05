package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "dbo.CentroCosto")
public class CecoEntity {

	@Id
	@Column(name = "Texto", insertable = false, updatable = false)
	private String texto;

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
}