package com.viaticos.domain;

import java.util.List;

public class PrepolizaElementos {

	private Object cabecera;
	private List<Object> comprobantes;
	private List<Object> nacionales;
	private List<Object> doctos;

	public List<Object> getDoctos() {
		return doctos;
	}

	public void setDoctos(List<Object> doctos) {
		this.doctos = doctos;
	}

	public List<Object> getNacionales() {
		return nacionales;
	}

	public void setNacionales(List<Object> nacionales) {
		this.nacionales = nacionales;
	}

	public List<Object> getComprobantes() {
		return comprobantes;
	}

	public void setComprobantes(List<Object> comprobantes) {
		this.comprobantes = comprobantes;
	}

	public Object getCabecera() {
		return cabecera;
	}

	public void setCabecera(Object cabecera) {
		this.cabecera = cabecera;
	}

	public PrepolizaElementos() {
		super();
		// TODO Auto-generated constructor stub
	}

}
