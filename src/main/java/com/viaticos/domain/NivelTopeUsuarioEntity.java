package com.viaticos.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nivel_tope_usuario")
public class NivelTopeUsuarioEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_nivel")
	private int id;
	
	@Column(name = "nivel")
	private int nivel;
	
	@Column(name = "total_alimentos_comida_por_dia", precision = 14, scale = 2)
	private BigDecimal totalAlimentosComidaDia;
	
	@Column(name = "total_alimentos_desayuno_por_dia", precision = 14, scale = 2)
	private BigDecimal totalAlimentosDesayunoDia;
	
	@Column(name = "total_alimentos_cena_por_dia", precision = 14, scale = 2)
	private BigDecimal totalAlimentosCenaDia;
	
	@Column(name = "hospedaje_por_dia", precision = 14, scale = 2)
	private BigDecimal hospedajeDia;
	
	@Column(name = "propina", precision = 14, scale = 2)
	private BigDecimal propina;
	
	@Column(name = "estacionamiento", precision = 14, scale = 2)
	private BigDecimal estacionamiento;
	
	@Column(name = "atencion_a_clientes", precision = 14, scale = 2)
	private BigDecimal atencionClientes;
	
	@Column(name = "talachas", precision = 14, scale = 2)
	private BigDecimal talachas;
	
	@Column(name = "no_deducibles", precision = 14, scale = 2)
	private BigDecimal noDeducibles;
	
	@Column(name = "taxis", precision = 14, scale = 2)
	private BigDecimal taxis;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public BigDecimal getHospedajeDia() {
		return hospedajeDia;
	}

	public void setHospedajeDia(BigDecimal hospedajeDia) {
		this.hospedajeDia = hospedajeDia;
	}

	public BigDecimal getPropina() {
		return propina;
	}

	public void setPropina(BigDecimal propina) {
		this.propina = propina;
	}

	public BigDecimal getEstacionamiento() {
		return estacionamiento;
	}

	public void setEstacionamiento(BigDecimal estacionamiento) {
		this.estacionamiento = estacionamiento;
	}

	public BigDecimal getAtencionClientes() {
		return atencionClientes;
	}

	public void setAtencionClientes(BigDecimal atencionClientes) {
		this.atencionClientes = atencionClientes;
	}

	public BigDecimal getTalachas() {
		return talachas;
	}

	public void setTalachas(BigDecimal talachas) {
		this.talachas = talachas;
	}

	public BigDecimal getNoDeducibles() {
		return noDeducibles;
	}

	public void setNoDeducibles(BigDecimal noDeducibles) {
		this.noDeducibles = noDeducibles;
	}

	public BigDecimal getTaxis() {
		return taxis;
	}

	public void setTaxis(BigDecimal taxis) {
		this.taxis = taxis;
	}

	public NivelTopeUsuarioEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getTotalAlimentosComidaDia() {
		return totalAlimentosComidaDia;
	}

	public void setTotalAlimentosComidaDia(BigDecimal totalAlimentosComidaDia) {
		this.totalAlimentosComidaDia = totalAlimentosComidaDia;
	}

	public BigDecimal getTotalAlimentosDesayunoDia() {
		return totalAlimentosDesayunoDia;
	}

	public void setTotalAlimentosDesayunoDia(BigDecimal totalAlimentosDesayunoDia) {
		this.totalAlimentosDesayunoDia = totalAlimentosDesayunoDia;
	}

	public BigDecimal getTotalAlimentosCenaDia() {
		return totalAlimentosCenaDia;
	}

	public void setTotalAlimentosCenaDia(BigDecimal totalAlimentosCenaDia) {
		this.totalAlimentosCenaDia = totalAlimentosCenaDia;
	}
	
}
