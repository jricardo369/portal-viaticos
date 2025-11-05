package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.EstatusSolicitudEntity;

public interface EstatusSolicitudPort {
	
	public EstatusSolicitudEntity obtieneEstatusSolicitud(int id);
	public List<EstatusSolicitudEntity> obtenerEstatus();
	public List<EstatusSolicitudEntity> obtenerEstatusList(List<Integer> estatus);

}
