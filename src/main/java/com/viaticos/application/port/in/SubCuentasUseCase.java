package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.SubCuentaContableEntity;

public interface SubCuentasUseCase {
	
	public SubCuentaContableEntity obtenerSubCuentaContable(int id);
	public List<SubCuentaContableEntity> obtenerSubCuentaContables();
	public void insertarSubCuentaContable(SubCuentaContableEntity scc);
	public void modificarSubCuentaContable(SubCuentaContableEntity scc,String usuario);
	public void eliminarSubCuentaContable(int id,String usuario);
	public List<SubCuentaContableEntity> obtenerSubCuentaEmpCeco(String empresa, String ceco);

}
