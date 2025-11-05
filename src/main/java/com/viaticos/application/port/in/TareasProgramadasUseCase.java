package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.TareaProgramadaEntity;

public interface TareasProgramadasUseCase {
	
	public List<TareaProgramadaEntity> obtenerTareasProgramadas();
	public TareaProgramadaEntity obtenerTareaProgramada(int codigo);
	public void insertarTareaProgramada(TareaProgramadaEntity tarea);
	public void actualizarTareaProgramada(TareaProgramadaEntity tarea,String usuario);

}
