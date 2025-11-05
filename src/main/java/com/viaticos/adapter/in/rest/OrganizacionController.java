package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.OrganizacionesUseCase;
import com.viaticos.domain.sql.nu3.OrgEntity;

@RequestMapping("/organizacion")
@RestController
public class OrganizacionController{
	
	@Autowired
	private OrganizacionesUseCase orgUseCase;
	
	@GetMapping
	public List<OrgEntity> obtenerOrganizaciones() {
		return orgUseCase.obtenerOrganizaciones();
	}
	
}
