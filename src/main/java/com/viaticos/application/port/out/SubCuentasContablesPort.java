package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.SubCuentaContableEntity;

public interface SubCuentasContablesPort {
	
	public SubCuentaContableEntity obtenerSubcuentaContable(int id);
	public List<SubCuentaContableEntity> obtenerSubcuentasContables();
	public void insertarSubCuentaContable(SubCuentaContableEntity scc);
	public void modificarSubCuentaContable(SubCuentaContableEntity scc);
	public void eliminarSubCuentaContable(int id);
	public List<SubCuentaContableEntity> obtenerSubCuentaEmpCeco(String empresa,String ceco);
    public SubCuentaContableEntity obtenerSubCuentaPorTipo(String empresa, String ceco, String tipo);
	SubCuentaContableEntity obtenerSubCuentaC(String codigo,String empresa, String ceco, String tipo);
}
