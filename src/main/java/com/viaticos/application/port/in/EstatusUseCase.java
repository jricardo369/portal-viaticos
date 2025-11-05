package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.EstatusSolicitudEntity;

public interface EstatusUseCase {
	
	public List<EstatusSolicitudEntity> obtenerEstatus();

}
