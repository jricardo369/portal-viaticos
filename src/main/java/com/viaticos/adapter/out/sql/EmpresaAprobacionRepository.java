package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EmpresaAprobacionPort;
import com.viaticos.application.port.out.jpa.mysql.EmpresaAprobacionJPA;
import com.viaticos.domain.EmpresaAprobacionEntity;

@Service
public class EmpresaAprobacionRepository implements EmpresaAprobacionPort {
	
	@Autowired
	private EmpresaAprobacionJPA enpJpa;

	@Override
	public List<EmpresaAprobacionEntity> obtenerEmpresasAprobacion() {
		return enpJpa.findAll();
	}

	@Override
	public void insertarEmpresaAprobacion(EmpresaAprobacionEntity empresa) {
		enpJpa.save(empresa);
	}

	@Override
	public void eliminarEmpresaAprobacion(int empresa) {
		EmpresaAprobacionEntity e = enpJpa.obtenerPorIdEmpAp(empresa);
				
		enpJpa.delete(e);
	}

	@Override
	public void modificarEmpresaAprobacion(EmpresaAprobacionEntity empresa) {
		enpJpa.save(empresa);
	}

	

}
