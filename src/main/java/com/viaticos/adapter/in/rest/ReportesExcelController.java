package com.viaticos.adapter.in.rest;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.viaticos.adapter.out.file.ExcelReporte;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.Solicitud;

@RequestMapping("/reportes-excel")
@RestController
public class ReportesExcelController {
	
	Logger log = LoggerFactory.getLogger(ReportesExcelController.class);

	@Autowired
	private SolicitudesDeUsuarioPort solPort;

	@Autowired
	private ExcelReporte excelReporte;

	@GetMapping("excel-reporte")
	public byte[] obtenerReporteExcelReporte(@RequestParam String usuario, @RequestParam String fechaInicio,
			@RequestParam String fechaFin, @RequestParam String estatus, @RequestParam String empresas,@RequestParam String evento,@RequestParam int numeroSolicitud)
			throws FileNotFoundException, DocumentException, ParseException {

		log.info(estatus);
		log.info(empresas);
		log.info(fechaInicio);
		log.info(fechaFin);
		log.info(usuario);
		log.info(""+numeroSolicitud);
//		String empresasss = empresas.replace(",", "','");
//		log.info(empresasss);

		List<Solicitud> solicitudes;
		byte[] salida = null;
		if(evento == null) 
			evento = "";
		
		if ("".equals(evento)) {
			solicitudes = solPort.obtenerSolicitudesReporte(estatus, empresas, fechaInicio, fechaFin,numeroSolicitud);
		} else {
			solicitudes = solPort.obtenerPorEventoYFechas(evento, fechaInicio, fechaFin);
		}
		System.out.println("Obtener solicitudes ok");
		salida = excelReporte.generarXLS(solicitudes, usuario, estatus, empresas, fechaInicio, fechaFin);
		System.out.println("Generar pdf");
		System.out.println("solicitudes:" + solicitudes.size());
		return salida;
	}

}
