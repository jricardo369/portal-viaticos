package com.viaticos.domain;

import java.math.BigDecimal;

public class Impuesto {

	private String impuesto;
	private BigDecimal tasaCuota;
	private BigDecimal importe;
	
	public String getImpuesto() {
		return impuesto;
	}
	public void setImpuesto(String impuesto) {
		this.impuesto = impuesto;
	}
	public BigDecimal getTasaCuota() {
		return tasaCuota;
	}
	public void setTasaCuota(BigDecimal tasaCuota) {
		this.tasaCuota = tasaCuota;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public Impuesto(String impuesto, BigDecimal tasaCuota, BigDecimal importe) {
		super();
		this.impuesto = impuesto;
		this.tasaCuota = tasaCuota;
		this.importe = importe;
	}
	
	
}
