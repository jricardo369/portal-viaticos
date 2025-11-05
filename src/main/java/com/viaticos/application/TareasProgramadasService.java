package com.viaticos.application;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.TareasProgramadasUseCase;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.application.port.out.TareasProgramadasPort;
import com.viaticos.domain.EventoConfiguracionEntity;
import com.viaticos.domain.TareaProgramadaEntity;

@Service
public class TareasProgramadasService implements TareasProgramadasUseCase{

	@Autowired
	private TareasProgramadasPort tareaPort;
	
	@Autowired
	private EventoConfiguracionPort eventoPort;

	
	@Override
	public List<TareaProgramadaEntity> obtenerTareasProgramadas() {
		return tareaPort.obtenerTareasProgramadas();
	}

	@Override
	public TareaProgramadaEntity obtenerTareaProgramada(int codigo) {
		if (codigo == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el código a buscar");

		}
		return tareaPort.obtenerTareaProgramada(codigo);
	}

	@Override
	public void insertarTareaProgramada(TareaProgramadaEntity tarea) {
		if (tarea == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar los valores de tarea programada a insertar");

		}

		if (tarea.getId() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el codigo de la tarea programada");

		}
		
		if (tarea.getDia() == null || "".equals(tarea.getDia())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el día de la tarea programada");

		}
		
		if (tarea.getHora() == null || "".equals(tarea.getHora())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar la hora de la tarea programada");

		}
		
		if (tarea.getDescripcion() == null || "".equals(tarea.getDescripcion())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar la descripción de la tarea programada");

		}
		
		tarea.setFechaModificacion(new Date());
		
		tareaPort.insertarTareaProgramada(tarea);
	}

	@Override
	public void actualizarTareaProgramada(TareaProgramadaEntity tarea,String usuario) {
		if (tarea == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar los valores de tarea programada a editar");

		}

		if (tarea.getId() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el id de la tarea programada");

		}

		if (tarea.getDia() == null || "".equals(tarea.getDia())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el día de la tarea programada");

		}
		
		if (tarea.getHora() == null || "".equals(tarea.getHora())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar la hora de la tarea programada");

		}
		
		if (tarea.getDescripcion() == null || "".equals(tarea.getDescripcion())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar la descripción de la tarea programada");

		}
		tarea.setFechaModificacion(new Date());
		
		tareaPort.actualizarTareaProgramada(tarea);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("CAMBIO TAREA PROGRAMADA");
		evento.setTexto("SE CAMBIO LA TAREA PROGRAMADA " +tarea.getCodigo());
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);
	}

	
	
}
