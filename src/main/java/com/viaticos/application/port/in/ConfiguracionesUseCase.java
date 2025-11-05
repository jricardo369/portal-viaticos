package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.ConfiguracionEntity;

public interface ConfiguracionesUseCase {
	
	public List<ConfiguracionEntity> obtenerConfiguraciones();
	public ConfiguracionEntity obtenerConfiguracion(int codigo);
	public void insertarConfiguracion(ConfiguracionEntity codigo);
	public void actualizarConfiguracion(ConfiguracionEntity codigo,String usuario);

}
