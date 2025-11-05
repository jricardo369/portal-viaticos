package com.viaticos.adapter.out.sql;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.SubCuentasContablesPort;
import com.viaticos.application.port.out.jpa.mysql.SubCuentasContablesJPA;
import com.viaticos.domain.SubCuentaContableEntity;

@Service
public class SubCuentaContableRepository implements SubCuentasContablesPort{
	
	@Autowired
	private SubCuentasContablesJPA sccJpa;

	@Override
	public SubCuentaContableEntity obtenerSubcuentaContable(int id) {
		return sccJpa.findById(id);
	}

	@Override
	public List<SubCuentaContableEntity> obtenerSubcuentasContables() {
		return sccJpa.findAll();
	}

	@Override
	public void insertarSubCuentaContable(SubCuentaContableEntity scc) {
		sccJpa.save(scc);
	}

	@Override
	public void eliminarSubCuentaContable(int id) {
		sccJpa.deleteById(id);
	}

	@Override
	public void modificarSubCuentaContable(SubCuentaContableEntity scc) {
		sccJpa.save(scc);
	}

	@Override
	public List<SubCuentaContableEntity> obtenerSubCuentaEmpCeco(String empresa, String ceco) {
		return sccJpa.obtenerSubCuentaEmpCeco(empresa, ceco);
	}
	
	@Override
	public SubCuentaContableEntity obtenerSubCuentaPorTipo(String empresa, String ceco, String tipo) {

	  return sccJpa.obtenerSubCuentaPorTipo(empresa, ceco, tipo);

	}
	
	@Override
	public SubCuentaContableEntity obtenerSubCuentaC(String codigo, String empresa, String ceco, String tipo) {

	  return sccJpa.obtenerSubCuentaC(codigo, empresa, ceco, tipo);

	}


}
