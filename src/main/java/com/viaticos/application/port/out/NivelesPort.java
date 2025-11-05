package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.NivelTopeUsuarioEntity;

public interface NivelesPort {
	
	public NivelTopeUsuarioEntity obtenerNivelPorId(int nivel);
	public NivelTopeUsuarioEntity obtenerNivelPorNivel(int nivel);
	public List<NivelTopeUsuarioEntity> obtenerNiveles();
	public void insertarNivel(NivelTopeUsuarioEntity nivel);
	public void eliminarNivel(int nivel);
	public void modificarNivel(NivelTopeUsuarioEntity nivel);
	
}
