package com.viaticos.application;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.NivelesUseCase;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.application.port.out.NivelesPort;
import com.viaticos.domain.EventoConfiguracionEntity;
import com.viaticos.domain.NivelTopeUsuarioEntity;

@Service
public class NivelesService implements NivelesUseCase{
	
	@Autowired
	private NivelesPort nivPort;
	
	@Autowired
	private EventoConfiguracionPort eventoPort;

	@Override
	public NivelTopeUsuarioEntity obtenerNivel(int nivel) {
		return nivPort.obtenerNivelPorId(nivel);
	}

	@Override
	public List<NivelTopeUsuarioEntity> obtenerNiveles() {
		return nivPort.obtenerNiveles();
	}

	@Override
	public void insertarNivel(NivelTopeUsuarioEntity nivel) {
		nivPort.insertarNivel(nivel);
	}

	@Override
	public void eliminarNivel(int nivel,String usuario) {
		nivPort.eliminarNivel(nivel);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("SE ELIMINO NIVEL");
		evento.setTexto("SE ELIMINO EL NIVEL " + nivel);
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);
	}

	@Override
	public void modificarNivel(NivelTopeUsuarioEntity nivel,String usuario) {
		nivPort.modificarNivel(nivel);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("CAMBIO EN NIVEL");
		evento.setTexto("SE CAMBIO EL NIVEL " + nivel.getNivel());
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);
	}

}
