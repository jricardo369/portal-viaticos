package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.TareasProgramadasPort;
import com.viaticos.application.port.out.jpa.mysql.TareasProgramadasJPA;
import com.viaticos.domain.TareaProgramadaEntity;

@Service
public class TareasProgramadasRepository  implements TareasProgramadasPort{
	
	@Autowired
	private TareasProgramadasJPA tareasJpa;

	@Override
	public List<TareaProgramadaEntity> obtenerTareasProgramadas() {
		return tareasJpa.findAll();
	}

	@Override
	public TareaProgramadaEntity obtenerTareaProgramada(int codigo) {
		return tareasJpa.findById(codigo);
	}

	@Override
	public void insertarTareaProgramada(TareaProgramadaEntity tarea) {
		tareasJpa.save(tarea);
		
	}

	@Override
	public void actualizarTareaProgramada(TareaProgramadaEntity tarea) {
		tareasJpa.save(tarea);
	}

}
