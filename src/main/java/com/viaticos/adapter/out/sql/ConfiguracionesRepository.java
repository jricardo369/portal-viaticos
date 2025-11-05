package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.jpa.mysql.ConfiguracionesJPA;
import com.viaticos.domain.ConfiguracionEntity;

@Service
public class ConfiguracionesRepository implements ConfiguracionesPort{
	
	@Autowired
	private ConfiguracionesJPA configJPA;

	@Override
	public List<ConfiguracionEntity> obtenerConfiguraciones() {
		return configJPA.findAll();
	}

	@Override
	public ConfiguracionEntity obtenerConfiguracion(int codigo) {
		return configJPA.findById(codigo);
	}

	@Override
	public void insertarConfiguracion(ConfiguracionEntity tarea) {
		configJPA.save(tarea);
	}

	@Override
	public void actualizarConfiguracion(ConfiguracionEntity tarea) {
		configJPA.save(tarea);
	}


}
