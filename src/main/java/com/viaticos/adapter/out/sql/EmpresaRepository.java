package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.jpa.mysql.EmpresaJPA;
import com.viaticos.domain.EmpresaEntity;

@Service
public class EmpresaRepository implements EmpresaPort {
	
	@Autowired
	private EmpresaJPA enpJpa;

	@Override
	public EmpresaEntity obtenerEmpresaPorId(int id) {
		return enpJpa.findById(id);
	}

	@Override
	public EmpresaEntity obtenerEmpresaPorEmpresa(String empresa) {
		return enpJpa.obtenerPorCodigoEmpresa(empresa);
	}
	
	@Override
	public EmpresaEntity obtenerEmpresaPorPorCodigoEmpresaCompleto(String empresa) {
		return enpJpa.obtenerPorCodigoEmpresaCompleto(empresa);
	}
	
	@Override
	public List<EmpresaEntity> obtenerEmpresasPorEmpresa(String empresa) {
		return enpJpa.obtenerEmpresasPorCodigo(empresa);
	}

	@Override
	public List<EmpresaEntity> obtenerEmpresa() {
		return enpJpa.findAll();
	}

	@Override
	public void insertarEmpresa(EmpresaEntity empresa) {
		enpJpa.save(empresa);
		
	}

	@Override
	public void eliminarEmpresa(int empresa) {
		enpJpa.deleteById(empresa);
		
	}

	@Override
	public void modificarEmpresa(EmpresaEntity empresa) {
		enpJpa.save(empresa);
	}

}
