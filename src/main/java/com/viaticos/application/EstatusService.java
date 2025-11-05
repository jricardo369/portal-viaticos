package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.in.EstatusUseCase;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.domain.EstatusSolicitudEntity;

@Service
public class EstatusService implements EstatusUseCase {

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Override
	public List<EstatusSolicitudEntity> obtenerEstatus() {
		return estatusPort.obtenerEstatus();
	}

	
}
