package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.EmpresaAprobacionEntity;

public interface EmpresaAprobacionUseCase {
	
	public List<EmpresaAprobacionEntity> obtenerEmpresasAprobacion();
	public void insertarEmpresaAprobacion(EmpresaAprobacionEntity empresa);
	public void eliminarEmpresaAprobacion(int empresa);
	public void modificarEmpresaAprobacion(EmpresaAprobacionEntity empresa);

}
