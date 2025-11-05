package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.Organizacion")
public class OrgEntity {

	@Id
	@Column(name = "ID_Organizacion", insertable = false, updatable = false)
	private String idOrganizacion;

	@Column(name = "Nombre", insertable = false, updatable = false)
	private String nombre;

	@Column(name = "Descripcion", insertable = false, updatable = false)
	private String description;

	@Column(name = "RFC", insertable = false, updatable = false)
	private String rfc;

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(String idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OrgEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
