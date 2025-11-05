package com.viaticos.domain.sql.nu3;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "dbo.Periodo")
public class PeriodoEntity {
	
	@Id
	@Column(name = "ID_Periodo", insertable = false, updatable = false)
	private int id;

	@JsonFormat(pattern="yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@Column(name = "FechaInicial", insertable = false, updatable = false)	
	private Date fecha;
	
	@Column(name = "Numero", insertable = false, updatable = false)
	private int numero;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	

}
