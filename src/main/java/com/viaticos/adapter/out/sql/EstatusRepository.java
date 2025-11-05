package com.viaticos.adapter.out.sql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.jpa.mysql.EstatusSolicitudJPA;
import com.viaticos.domain.EstatusSolicitudEntity;

@Service
public class EstatusRepository implements EstatusSolicitudPort {

	@Autowired
	private EstatusSolicitudJPA estatusJpa;

	@Override
	public EstatusSolicitudEntity obtieneEstatusSolicitud(int id) {
		EstatusSolicitudEntity estatusEnt = new EstatusSolicitudEntity();
		estatusEnt = estatusJpa.findById(id);
		return estatusEnt;
	}

	@Override
	public List<EstatusSolicitudEntity> obtenerEstatus() {
		List<EstatusSolicitudEntity> estatusEnt = new ArrayList<>();
		estatusEnt = estatusJpa.obtenerEstatus();
		return estatusEnt;
	}
	
	@Override
	public List<EstatusSolicitudEntity> obtenerEstatusList(List<Integer> estatus) {
		return estatusJpa.buscaEstatusList(estatus);
		
	}

}
