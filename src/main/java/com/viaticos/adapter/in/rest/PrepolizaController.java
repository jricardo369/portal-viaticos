package com.viaticos.adapter.in.rest;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.DocumentException;
import com.viaticos.adapter.out.file.ExcelPoliza;
import com.viaticos.application.port.in.PrepolizaUseCase;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.Respuesta;
import com.viaticos.domain.SolicitudViaticosEntity;

@RequestMapping("/prepolizas")
@RestController
public class PrepolizaController {
	
	Logger log = LoggerFactory.getLogger(PrepolizaController.class);
	
	@Autowired
	private PrepolizaUseCase polizaUseCase;
	
	@Autowired
	private EmpresaPort empPort;
	
	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;
	
	@Autowired
	private ExcelPoliza excelPoliza;

	@GetMapping("{numeroSolicitud}")
	public List<Prepoliza> obtenerPrepoliza(@PathVariable("numeroSolicitud") int numeroSolicitud) {
		
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		String empresaSolicitud = "";
		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);
		}
		
		if (solicitud.getEmpresa() != null) {
			empresaSolicitud = solicitud.getEmpresa().trim();
		}
		
		EmpresaEntity empresa = empPort.obtenerEmpresaPorEmpresa(empresaSolicitud);
		if(empresa == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"La empresa con código " + solicitud.getEmpresa().trim() + " no esta dada de alta, revisar con el administrador del sistema");
			
		}
		
		return polizaUseCase.generarPoliza(numeroSolicitud,true);
	}
	
	@GetMapping("excel-reporte/{numeroSolicitud}")
	public Respuesta obtenerExcelPolizas(@PathVariable("numeroSolicitud") int numeroSolicitud)
			throws FileNotFoundException, DocumentException, ParseException {

		Respuesta r = new Respuesta();
		log.info("Numero de solicitud:"+numeroSolicitud);

		byte[] salida = null;
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		String empresaSolicitud = "";
		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);
		}
		
		if (solicitud.getEmpresa() != null) {
			empresaSolicitud = solicitud.getEmpresa().trim();
		}
		
		EmpresaEntity empresa = empPort.obtenerEmpresaPorEmpresa(empresaSolicitud);
		if(empresa == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"La empresa con código " + solicitud.getEmpresa().trim() + " no esta dada de alta, revisar con el administrador del sistema");
			
		}
		
		List<Prepoliza> prepolizas = null;
		try {
			prepolizas = polizaUseCase.generarPoliza(numeroSolicitud, true);
		} catch (NullPointerException e) {
			r.setEstatus(500);
			r.setMensaje(e.getMessage());
			return r;
		}

		System.out.println("Obtener solicitudes ok");
		System.out.println("Empresa:"+empresa.getSistema());
		salida = excelPoliza.generarXLS(prepolizas, solicitud,empresa.getSistema());
		System.out.println("Generar excel");
		r.setEstatus(200);
		r.setArchivo(salida);
		return r;
	}

}
