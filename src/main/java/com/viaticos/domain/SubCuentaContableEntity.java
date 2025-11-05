package com.viaticos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sub_cuenta_contable")
public class SubCuentaContableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_subcuenta_contable")
	private int id;

	@Column(name = "codigo", length = 10)
	private String codigo;

	@Column(name = "descripcion", length = 45)
	private String descripcion;
	
	@Column(name = "empresa", length = 45)
	private String empresa;

	@Column(name = "ceco", length = 45)
	private String ceco;
	
	@Column(name = "tipo", length = 45)
	private String tipo;
	
	@Column(name = "tip_gasto", length = 10)
	private String tipoGasto;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
	public String getCeco() {
		return ceco;
	}

	public void setCeco(String ceco) {
		this.ceco = ceco ;
	}
		
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	

	public String getTipoGasto() {
		return tipoGasto;
	}

	public void setTipoGasto(String tipoGasto) {
		this.tipoGasto = tipoGasto;
	}

	/*
	 * public List<ComprobanteViaticoEntity> getComprobanteViaticoEntity() { return
	 * comprobanteViaticoEntity; }
	 * 
	 * public void setComprobanteViaticoEntity(List<ComprobanteViaticoEntity>
	 * comprobanteViaticoEntity) { this.comprobanteViaticoEntity =
	 * comprobanteViaticoEntity; }
	 */
	public SubCuentaContableEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	


}
