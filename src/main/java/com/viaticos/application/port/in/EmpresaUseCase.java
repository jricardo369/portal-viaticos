package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.EmpresaEntity;

public interface EmpresaUseCase {
	
    public EmpresaEntity obtenerEmpresa(String empresa);
    public EmpresaEntity obtenerEmpresaCompleto(String empresa);
	public List<EmpresaEntity> obtenerEmpresa();
	public void insertarEmpresa(EmpresaEntity empresa);
	public void modificarEmpresa(EmpresaEntity empresa);
	public void eliminarEmpresa(int empresa);

}
