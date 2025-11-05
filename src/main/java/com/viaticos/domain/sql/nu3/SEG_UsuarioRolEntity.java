package com.viaticos.domain.sql.nu3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "dbo.SEG_UsuarioRol")
public class SEG_UsuarioRolEntity {

	@Id
	@Column(name = "SUR_ID", insertable = false, updatable = false)
	private int surId;

	@Column(name = "US_ID", insertable = false, updatable = false)
	private int usId;

	@Column(name = "GRP_ID", insertable = false, updatable = false)
	private int grpId;

	@Column(name = "SUR_Miembro", insertable = false, updatable = false)
	private boolean surMiembro;

	/*@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_Id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private SEG_GruposEntity segGrupo;

	public SEG_GruposEntity getSegGrupo() {
		return segGrupo;
	}

	public void setSegGrupo(SEG_GruposEntity segGrupo) {
		this.segGrupo = segGrupo;
	}
*/
	public int getSurId() {
		return surId;
	}

	public void setSurId(int surId) {
		this.surId = surId;
	}

	public int getUsId() {
		return usId;
	}

	public void setUsId(int usId) {
		this.usId = usId;
	}

	public int getGrpId() {
		return grpId;
	}

	public void setGrpId(int grpId) {
		this.grpId = grpId;
	}

	public boolean isSurMiembro() {
		return surMiembro;
	}

	public void setSurMiembro(boolean surMiembro) {
		this.surMiembro = surMiembro;
	}

	public SEG_UsuarioRolEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
