package com.viaticos.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.application.port.in.OrganizacionesUseCase;
import com.viaticos.application.port.out.OrganizacionesPort;
import com.viaticos.domain.sql.nu3.OrgEntity;

@Service
public class OrganizacionesService implements OrganizacionesUseCase {

	@Autowired
	private OrganizacionesPort orgPort;

	@Override
	public List<OrgEntity> obtenerOrganizaciones() {
		List<OrgEntity> list = orgPort.obtenerOrganizaciones(); 
		if(list == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron organizaciones");

		return list;
	}

	
}
