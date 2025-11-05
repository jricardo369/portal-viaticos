package com.viaticos.domain;

public class EstatusSolicitudModel {
	

	private int id;
	private String descripcion;
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
	public EstatusSolicitudModel(int id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}
	public EstatusSolicitudModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
