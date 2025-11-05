package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.NivelesPort;
import com.viaticos.application.port.out.jpa.mysql.NivelesJPA;
import com.viaticos.domain.NivelTopeUsuarioEntity;

@Service
public class NivelRepository implements NivelesPort {
	
	@Autowired
	private NivelesJPA nivJpa;

	@Override
	public NivelTopeUsuarioEntity obtenerNivelPorId(int id) {
		return nivJpa.findById(id);
	}
	
	@Override
	public NivelTopeUsuarioEntity obtenerNivelPorNivel(int nivel) {
		return nivJpa.findByNivel(nivel);
	}

	@Override
	public List<NivelTopeUsuarioEntity> obtenerNiveles() {
		return nivJpa.findAll();
	}

	@Override
	public void insertarNivel(NivelTopeUsuarioEntity nivel) {
		nivJpa.save(nivel);
	}

	@Override
	public void eliminarNivel(int nivel) {
		nivJpa.deleteById(nivel);
	}

	@Override
	public void modificarNivel(NivelTopeUsuarioEntity nivel) {
		nivJpa.save(nivel);
	}

}
