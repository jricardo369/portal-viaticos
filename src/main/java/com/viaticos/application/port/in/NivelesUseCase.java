package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.NivelTopeUsuarioEntity;

public interface NivelesUseCase {
	
	public NivelTopeUsuarioEntity obtenerNivel(int nivel);
	public List<NivelTopeUsuarioEntity> obtenerNiveles();
	public void insertarNivel(NivelTopeUsuarioEntity nivel);
	public void modificarNivel(NivelTopeUsuarioEntity nivel,String usuario);
	public void eliminarNivel(int nivel,String usuario);

}
