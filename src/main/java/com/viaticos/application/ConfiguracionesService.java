package com.viaticos.application;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.ConfiguracionesUseCase;
import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.domain.ConfiguracionEntity;
import com.viaticos.domain.EventoConfiguracionEntity;

@Service
public class ConfiguracionesService implements ConfiguracionesUseCase {

	@Autowired
	private ConfiguracionesPort configPort;
	
	@Autowired
	private EventoConfiguracionPort eventoPort;

	@Override
	public List<ConfiguracionEntity> obtenerConfiguraciones() {
		return configPort.obtenerConfiguraciones();
	}

	@Override
	public ConfiguracionEntity obtenerConfiguracion(int codigo) {
		if (codigo == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el código a buscar");
		}
		return configPort.obtenerConfiguracion(codigo);
	}

	@Override
	public void insertarConfiguracion(ConfiguracionEntity codigo) {
		if (codigo == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar los valores de configuración a insertar");
		}

		if (codigo.getId() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el id de la configuración");
		}

		if (codigo.getValor1() == null || "".equals(codigo.getValor1())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar el valor de la configuración");
		}
		configPort.insertarConfiguracion(codigo);
	}

	@Override
	public void actualizarConfiguracion(ConfiguracionEntity codigo,String usuario) {
		if (codigo == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar los valores de configuración a editar");
		}

		if (codigo.getId() == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es necesario enviar el id de la configuración");

		}

		if (codigo.getValor1() == null || "".equals(codigo.getValor1())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Es necesario enviar el valor de la configuración");

		}

		configPort.actualizarConfiguracion(codigo);
		EventoConfiguracionEntity evento = new EventoConfiguracionEntity();
		evento.setEvento("CAMBIO EN CONFIGURACION");
		evento.setTexto("SE CAMBIO LA CONFIGURACION");
		evento.setFecha(new Date());
		evento.setUsuario(usuario);
		eventoPort.insertarEvento(evento);
		

	}

}
