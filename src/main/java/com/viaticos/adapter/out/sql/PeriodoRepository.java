package com.viaticos.adapter.out.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.PeriodoPort;
import com.viaticos.application.port.out.jpa.nu3.PeriodoJPA;
import com.viaticos.domain.sql.nu3.PeriodoEntity;

@Service
public class PeriodoRepository implements PeriodoPort {

	@Autowired
	private PeriodoJPA perJpa;

	@Override
	public int obtenerPeriodo(String fecha,String ejercicio) throws Exception {
		
		int periodo = 0;
		
		PeriodoEntity p;
		p = perJpa.findByFecha(fecha,ejercicio);
		if (p != null) {
			periodo = p.getNumero();
		}
		return periodo;
		
	}

}
