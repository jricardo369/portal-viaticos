package com.viaticos.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "evento_de_viatico")
public class EventoViaticoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_evento")
	private int id;

	@Column(name = "fecha")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;
	
	@Column(name = "evento", length = 120)
	private String evento;
	
	@Column(name = "texto", length = 200)
	private String texto;
	
	@Column(name = "usuario", length = 150)
	private String usuario;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "numero_solicitud", nullable = false)
	private SolicitudViaticosEntity numero_solicitud;

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

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public SolicitudViaticosEntity getNumero_solicitud() {
		return numero_solicitud;
	}

	public void setNumero_solicitud(SolicitudViaticosEntity numero_solicitud) {
		this.numero_solicitud = numero_solicitud;
	}

	public EventoViaticoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
