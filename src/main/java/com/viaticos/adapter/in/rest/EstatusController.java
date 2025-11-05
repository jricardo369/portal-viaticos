package com.viaticos.adapter.in.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.in.EstatusUseCase;
import com.viaticos.domain.EstatusSolicitudEntity;

@RequestMapping("/estatus")
@RestController
public class EstatusController{
	
	@Autowired
	private EstatusUseCase estatusUseCase;
	
	@GetMapping
	public List<EstatusSolicitudEntity> obtenerEstatus() {
		return estatusUseCase.obtenerEstatus();
	}
	
}
