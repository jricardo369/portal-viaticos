package com.viaticos.domain;

public class layoutComprobacion implements Comparable<layoutComprobacion> {

	private String usuario;
	private String tipo;
	private Double monto;
	private String referencia;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String toString() {
		return usuario + "," + tipo + "," + monto + "," + referencia;
	}

	public layoutComprobacion() {
		super();
	}

	@Override
	public int compareTo(layoutComprobacion o) {
		if (getUsuario() == null || o.getUsuario() == null) {
			return 0;
		}
		return getUsuario().compareTo(o.getUsuario());
	}

}
