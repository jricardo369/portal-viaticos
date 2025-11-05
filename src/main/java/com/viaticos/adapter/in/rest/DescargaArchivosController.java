package com.viaticos.adapter.in.rest;


import java.util.ArrayList;
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

import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.ComprobantesDeViaticosPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.SolicitudViaticosEntity;

@RequestMapping("/archivo")
@RestController
public class DescargaArchivosController {

	Logger log = LoggerFactory.getLogger(DescargaArchivosController.class);
	
	@Autowired
	private ArchivosPort archPort;
	
	@Autowired
	ComprobantesDeViaticosPort compPort;
	
	@Autowired	
	SolicitudesDeUsuarioPort solicitudesDeUsuarioPort;
	
	@GetMapping("/{formato}/{idComprobante}")
	public byte[] obtenerArchivoComrobante(@PathVariable String formato,@PathVariable int idComprobante) {
		
		if (formato != null && (!"pdf".equalsIgnoreCase(formato) && !"xml".equalsIgnoreCase(formato))) //
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato '" + formato + "' no valido");
		
		//Obtener comprobante
		ComprobanteViaticoEntity c = new ComprobanteViaticoEntity();

		c = compPort.obtenerDeComprobante(idComprobante);

		if (c == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, no se encontró el comprobante" + idComprobante);

		
		byte[] bytes = null;
		if(formato.equals("xml")) {
			bytes = archPort.obtenerArchivo(c.getRutaXml());
		}else {
			bytes = archPort.obtenerArchivo(c.getRutaPdf());
		}
		
		return bytes;
	}
	
	@GetMapping("/{idSolicitud}")
	public byte[] obtenerZipSolicitud(@PathVariable int idSolicitud) {
		SolicitudViaticosEntity s = solicitudesDeUsuarioPort.obtenerSolicitudJPA(idSolicitud);
		List<ComprobanteViaticoEntity> comprobantes = null;
		if(s==null) {
			comprobantes = new ArrayList<>();
		}else {
			comprobantes = s.getComprobanteViaticosEntity();
			log.info("Número de comprobantes"+comprobantes.size());
		}
		return archPort.obtenerArchivosSolicitudZip(comprobantes);
	}

}
