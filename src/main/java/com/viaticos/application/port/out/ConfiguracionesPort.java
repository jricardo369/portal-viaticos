package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.ConfiguracionEntity;

public interface ConfiguracionesPort {

	public List<ConfiguracionEntity> obtenerConfiguraciones();
	public ConfiguracionEntity obtenerConfiguracion(int codigo);
	public void insertarConfiguracion(ConfiguracionEntity codigo);
	public void actualizarConfiguracion(ConfiguracionEntity codigo);
	
}
