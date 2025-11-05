package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.EmpresaAprobacionUseCase;
import com.viaticos.application.port.out.EmpresaAprobacionPort;
import com.viaticos.domain.EmpresaAprobacionEntity;

@Service
public class EmpresaAprobacionService implements EmpresaAprobacionUseCase {
	
	@Autowired
	private EmpresaAprobacionPort empApPort;

	@Override
	public List<EmpresaAprobacionEntity> obtenerEmpresasAprobacion() {
		return empApPort.obtenerEmpresasAprobacion();
	}

	@Override
	public void insertarEmpresaAprobacion(EmpresaAprobacionEntity empresa) {
		empApPort.insertarEmpresaAprobacion(empresa);
	}

	@Override
	public void eliminarEmpresaAprobacion(int empresa) {
		empApPort.eliminarEmpresaAprobacion(empresa);
	}

	@Override
	public void modificarEmpresaAprobacion(EmpresaAprobacionEntity empresa) {
		empApPort.modificarEmpresaAprobacion(empresa);
	}

	

	

}
