package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "dbo.SEG_UsuarioOrganizacion")
public class SEG_UsuarioOrganizacionEntity {

	@Id
	@Column(name = "SUA_ID", insertable = false, updatable = false)
	private int suaId;

	@Column(name = "US_Id", insertable = false, updatable = false)
	private int udId;

	@Column(name = "ID_Organizacion", insertable = false, updatable = false)
	private String idOrganizacion;

	@Column(name = "SUA_Default", insertable = false, updatable = false)
	private boolean suaDefault;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ID_Organizacion", referencedColumnName = "ID_Organizacion", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private OrgEntity org;

	public int getSuaId() {
		return suaId;
	}

	public void setSuaId(int suaId) {
		this.suaId = suaId;
	}

	public int getUdId() {
		return udId;
	}

	public void setUdId(int udId) {
		this.udId = udId;
	}

	public String getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(String idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public boolean isSuaDefault() {
		return suaDefault;
	}

	public void setSuaDefault(boolean suaDefault) {
		this.suaDefault = suaDefault;
	}

	public OrgEntity getOrg() {
		return org;
	}

	public void setOrg(OrgEntity org) {
		this.org = org;
	}

	public SEG_UsuarioOrganizacionEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
