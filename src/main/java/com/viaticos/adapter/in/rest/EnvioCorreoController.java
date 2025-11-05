package com.viaticos.adapter.in.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.out.EnviarCorreoPort;

@RequestMapping("/envio-correo")
@RestController
public class EnvioCorreoController {
	
	@Autowired
	private EnviarCorreoPort envioCorreoPort;
	
	@GetMapping("solicitud-aprobacion")
	public void envioCorreoSolicitid(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		//envioCorreoPort.enviarCorreoSolicitudAprobacionSolicitante(email, usuario, params);
	}
	
	@GetMapping("aprobacion-sol")
	public void envioCorreoAprobacion(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		envioCorreoPort.enviarCorreoAprobacionSolicitud(email, usuario, params);
	}
	
	@GetMapping("rechazo-sol")
	public void envioCorreoRechazo(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		params.put("${motivo}", "El monto que se solicito no es el autorizado por SLAPI");
		envioCorreoPort.enviarCorreoRechazoSolicitud(email, usuario, params);
	}
	
	@GetMapping("aprobacion-comp")
	public void envioCorreoAprobComp(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		params.put("${motivo}", "El monto que se solicito no es el autorizado por SLAPI");
		envioCorreoPort.enviarCorreoAprobacionComprobacion(email, usuario, params);
	}
	
	@GetMapping("rechazo-comp")
	public void envioCorreoRechazoComp(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		params.put("${motivo}", "El monto que se solicito no es el autorizado por SLAPI");
		envioCorreoPort.enviarCorreoRechazoComprobacion(email, usuario, params);
	}
	
	@GetMapping("aprobacion-comp-final")
	public void envioCorreoAprobCompFinal(@RequestParam("email") String email, @RequestParam("usuario") String usuario) {
		Map<String, Object> params = new HashMap<>();
		params.put("${usuario-aprobador}", "Jose Lopez");
		params.put("${usuario-solicitante}", "Ricardo Ramirez");
		params.put("${numero-solicitud}", "22");
		params.put("${motivo}", "El monto que se solicito no es el autorizado por SLAPI");
		envioCorreoPort.enviarCorreoAprobacionComprobacionFinal(email, usuario, params);
	}

}
