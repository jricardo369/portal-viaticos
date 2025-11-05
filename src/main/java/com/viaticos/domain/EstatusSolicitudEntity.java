package com.viaticos.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "estatus_solicitud")
public class EstatusSolicitudEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_estatus")
	private int id;
	
	@Column(name = "descripcion", length = 45)
	private String descripcion;
	
	@JsonIgnore
	@OneToMany(mappedBy = "estatus", cascade = CascadeType.ALL)
	private List<SolicitudViaticosEntity> solicitudViaticosEntity;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<SolicitudViaticosEntity> getSolicitudViaticosEntity() {
		return solicitudViaticosEntity;
	}

	public void setSolicitudViaticosEntity(List<SolicitudViaticosEntity> solicitudViaticosEntity) {
		this.solicitudViaticosEntity = solicitudViaticosEntity;
	}

	public EstatusSolicitudEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
