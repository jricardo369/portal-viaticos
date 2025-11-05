package com.viaticos.domain;

import java.math.BigDecimal;

public class Prepoliza {

	public String fecha;
	public String tipoPoliza;
	public String subCuenta;
	public String concepto;
	public String uuid;
	public String rfc;
	public BigDecimal cargo;
	public BigDecimal abono;
	public String ceco;
	public String flujo;
	public String tipo;
	public String posicion;
	public String tipoGasto;

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTipoPoliza() {
		return tipoPoliza;
	}

	public void setTipoPoliza(String tipoPoliza) {
		this.tipoPoliza = tipoPoliza;
	}

	public String getSubCuenta() {
		return subCuenta;
	}

	public void setSubCuenta(String subCuenta) {
		this.subCuenta = subCuenta;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public BigDecimal getCargo() {
		return cargo;
	}

	public void setCargo(BigDecimal cargo) {
		this.cargo = cargo;
	}

	public BigDecimal getAbono() {
		return abono;
	}

	public void setAbono(BigDecimal abono) {
		this.abono = abono;
	}

	public String getCeco() {
		return ceco;
	}

	public void setCeco(String ceco) {
		this.ceco = ceco;
	}

	public String getFlujo() {
		return flujo;
	}

	public void setFlujo(String flujo) {
		this.flujo = flujo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getTipoGasto() {
		return tipoGasto;
	}

	public void setTipoGasto(String tipoGasto) {
		this.tipoGasto = tipoGasto;
	}

	public Prepoliza(String fecha, String tipoPoliza, String subCuenta, String concepto, String uuid, String rfc,
			BigDecimal cargo, BigDecimal abono, String ceco, String flujo,String tipo,String posicion,String tipoGasto) {
		super();
		this.fecha = fecha;
		this.tipoPoliza = tipoPoliza;
		this.subCuenta = subCuenta;
		this.concepto = concepto;
		this.uuid = uuid;
		this.rfc = rfc;
		this.cargo = cargo;
		this.abono = abono;
		this.ceco = ceco;
		this.flujo = flujo;
		this.tipo = tipo;
		this.posicion = posicion;
		this.tipoGasto = tipoGasto;
	}

	
	
}
