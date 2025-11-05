package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.EmpresaEntity;

public interface EmpresaPort {
	
	    public EmpresaEntity obtenerEmpresaPorId(int id);
		public EmpresaEntity obtenerEmpresaPorEmpresa(String empresa);
		public EmpresaEntity obtenerEmpresaPorPorCodigoEmpresaCompleto(String empresa);
		public List<EmpresaEntity> obtenerEmpresasPorEmpresa(String empresa);
		public List<EmpresaEntity> obtenerEmpresa();
		public void insertarEmpresa(EmpresaEntity empresa);
		public void eliminarEmpresa(int empresa);
		public void modificarEmpresa(EmpresaEntity empresa);

}
