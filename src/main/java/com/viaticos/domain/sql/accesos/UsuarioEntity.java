package com.viaticos.domain.sql.accesos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.SEG_Usuario")
public class UsuarioEntity {

	@Id
	@Column(name = "US_Id", insertable = false, updatable = false)
	private int id;

	@Column(name = "US_NCorto", insertable = false, updatable = false)
	private String usNCorto;

	@Column(name = "US_Nombres", insertable = false, updatable = false)
	private String usNombre;

	@Column(name = "US_Psw", insertable = false, updatable = false)
	private String usPw;

	@Column(name = "US_EsAdmin", insertable = false, updatable = false)
	private boolean usEsAdmin;

	@Column(name = "US_Email", insertable = false, updatable = false)
	private String usEmail;

	@Column(name = "US_Activo", insertable = false, updatable = false)
	private boolean usActivo;

	public boolean isUsActivo() {
		return usActivo;
	}

	public void setUS_Activo(boolean usActivo) {
		this.usActivo = usActivo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsNCorto() {
		return usNCorto;
	}

	public void setUsNCorto(String usNCorto) {
		this.usNCorto = usNCorto;
	}

	public String getUsPw() {
		return usPw;
	}

	public void setUsPw(String usPw) {
		this.usPw = usPw;
	}

	public boolean isUsEsAdmin() {
		return usEsAdmin;
	}

	public void setUsEsAdmin(boolean usEsAdmin) {
		this.usEsAdmin = usEsAdmin;
	}

	public String getUsNombre() {
		return usNombre;
	}

	public void setUsNombre(String usNombre) {
		this.usNombre = usNombre;
	}

	public String getUsEmail() {
		return usEmail;
	}

	public void setUsEmail(String usEmail) {
		this.usEmail = usEmail;
	}

	public UsuarioEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
