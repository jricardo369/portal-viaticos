package com.viaticos.application.port.out;

import java.util.List;

import com.viaticos.domain.sql.nu3.OrgEntity;

public interface OrganizacionesPort {
	
	public List<OrgEntity> obtenerOrganizaciones();

}
