package com.viaticos.application.port.in;

import java.util.List;

import com.viaticos.domain.sql.nu3.OrgEntity;

public interface OrganizacionesUseCase {
	
	public List<OrgEntity> obtenerOrganizaciones();

}
