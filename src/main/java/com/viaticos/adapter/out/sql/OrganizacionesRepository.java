package com.viaticos.adapter.out.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.application.port.out.OrganizacionesPort;
import com.viaticos.application.port.out.jpa.nu3.OrganizacionesJPA;
import com.viaticos.domain.sql.nu3.OrgEntity;

@Service
public class OrganizacionesRepository implements OrganizacionesPort {

	@Autowired
	private OrganizacionesJPA orgJPA;

	@Override
	public List<OrgEntity> obtenerOrganizaciones() {
		return orgJPA.obtenerOrganizacionesConRFC();
	}

}
