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
@Table(name = "dbo.Empleado")
public class EmpleadoEntity {

	@Id
	@Column(name = "ID_Empleado", insertable = false, updatable = false)
	private int id;

	@Column(name = "NombreCompleto", insertable = false, updatable = false)
	private String nombreCompleto;

	@Column(name = "Estatus", insertable = false, updatable = false)
	private String estatus;

	@Column(name = "CorreoElectronico", insertable = false, updatable = false)
	private String correoElectronico;

	@Column(name = "UserName", insertable = false, updatable = false)
	private String userName;

	@Column(name = "PasswordPortal", insertable = false, updatable = false)
	private String passPortal;

	@Column(name = "ID_Organizacion", insertable = false, updatable = false)
	private String idOrganizacion;

	@Column(name = "ID_Grupo01", insertable = false, updatable = false)
	private String idGrupo01;

	@Column(name = "ID_Departamento", insertable = false, updatable = false)
	private String idDepartamento;

	@Column(name = "ID_Clasificacion", insertable = false, updatable = false)
	private String idClasificacion;

	@Column(name = "Foto", insertable = false, updatable = false)
	private String foto;

	@Column(name = "Texto08", insertable = false, updatable = false)
	private String cuentaContable;

	@Column(name = "RFC", insertable = false, updatable = false)
	private String rfc;
	
	@Column(name = "Texto11", insertable = false, updatable = false)
	private String proyecto;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ID_TablaAd04", referencedColumnName = "ID_TablaAd04", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private TablaAd04Entity idTablaAd04;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ID_CentroCosto", referencedColumnName = "ID_CentroCosto", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private CentroCostoEntity idCentroCosto;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ID_Grupo01", referencedColumnName = "ID_Grupo01", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Grupo01Entity grupo01En;

	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "ID_Departamento", referencedColumnName = "ID_Departamento", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private DepartamentoEntity depEnt;

	public TablaAd04Entity getIdTablaAd04() {
		return idTablaAd04;
	}

	public void setIdTablaAd04(TablaAd04Entity idTablaAd04) {
		this.idTablaAd04 = idTablaAd04;
	}

	public CentroCostoEntity getIdCentroCosto() {
		return idCentroCosto;
	}

	public void setIdCentroCosto(CentroCostoEntity idCentroCosto) {
		this.idCentroCosto = idCentroCosto;
	}

	public String getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getIdClasificacion() {
		return idClasificacion;
	}

	public void setIdClasificacion(String idClasificacion) {
		this.idClasificacion = idClasificacion;
	}

	public DepartamentoEntity getDepEnt() {
		return depEnt;
	}

	public void setDepEnt(DepartamentoEntity depEnt) {
		this.depEnt = depEnt;
	}

	public String getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(String idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public Grupo01Entity getGrupo01En() {
		return grupo01En;
	}

	public void setGrupo01En(Grupo01Entity grupo01En) {
		this.grupo01En = grupo01En;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassPortal() {
		return passPortal;
	}

	public void setPassPortal(String passPortal) {
		this.passPortal = passPortal;
	}

	public String getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(String idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public String getIdGrupo01() {
		return idGrupo01;
	}

	public void setIdGrupo01(String idGrupo01) {
		this.idGrupo01 = idGrupo01;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

	public EmpleadoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}