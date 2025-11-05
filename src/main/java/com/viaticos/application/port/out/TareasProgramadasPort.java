package com.viaticos.application.port.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.viaticos.domain.TareaProgramadaEntity;

@Repository
public interface TareasProgramadasPort{

	public List<TareaProgramadaEntity> obtenerTareasProgramadas();
	public TareaProgramadaEntity obtenerTareaProgramada(int codigo);
	public void insertarTareaProgramada(TareaProgramadaEntity tarea);
	public void actualizarTareaProgramada(TareaProgramadaEntity tarea);
	
}
