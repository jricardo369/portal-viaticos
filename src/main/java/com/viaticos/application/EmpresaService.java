package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.EmpresaUseCase;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.domain.EmpresaEntity;

@Service
public class EmpresaService implements EmpresaUseCase {
	
	@Autowired
	private EmpresaPort empPort;

	@Override
	public EmpresaEntity obtenerEmpresa(String empresa) {
		return empPort.obtenerEmpresaPorEmpresa(empresa);
	}
	
	@Override
	public EmpresaEntity obtenerEmpresaCompleto(String empresa) {
		return empPort.obtenerEmpresaPorPorCodigoEmpresaCompleto(empresa);
	}

	@Override
	public List<EmpresaEntity> obtenerEmpresa() {
		return empPort.obtenerEmpresa();
	}

	@Override
	public void insertarEmpresa(EmpresaEntity empresa) {
		empPort.insertarEmpresa(empresa);
		
	}

	@Override
	public void modificarEmpresa(EmpresaEntity empresa) {
		empPort.modificarEmpresa(empresa);
		
	}

	@Override
	public void eliminarEmpresa(int empresa) {
		empPort.eliminarEmpresa(empresa);
	}

}
