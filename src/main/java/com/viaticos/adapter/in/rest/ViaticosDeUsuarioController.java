package com.viaticos.adapter.in.rest;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;
import com.viaticos.adapter.out.file.EventosAdapter;
import com.viaticos.application.port.in.ViaticosDeUsuarioUseCase;
import com.viaticos.domain.Comprobante;
import com.viaticos.domain.Solicitud;

@RequestMapping("/viaticos-usuario")
@RestController
public class ViaticosDeUsuarioController {
	
	Logger log = LoggerFactory.getLogger(ViaticosDeUsuarioController.class);

	@Autowired
	private ViaticosDeUsuarioUseCase viaticosUsUseCase;
	
	@Autowired
	private EventosAdapter ea;

	@GetMapping("{empleado}")
	public List<Solicitud> consultarSolicitudesDeUsuarioPorEstatus(@PathVariable("empleado") String empleado,
			@RequestParam("estatus") String estatus) {

		return viaticosUsUseCase.consultarSolicitudesDeUsuarioPorEstatusJPA(empleado, estatus);
	}

	@GetMapping("solicitudes-de-viaticos")
	public List<Solicitud> consultarSolicitudesPorEmpresasYEstatus(@RequestParam("empresas") String empresas,
			@RequestParam("estatus") String estatus, @RequestParam("usuario") String usuario) {

		return viaticosUsUseCase.obtenerSolicitudesPorEmpresasEstatus(empresas, estatus, usuario);
	}

	@GetMapping("solicitudes-de-viaticos/director")
	public List<Solicitud> solicitudesDirector(@RequestParam String fechaInicio, @RequestParam String fechaFin,
			@RequestParam String estatus, @RequestParam String empresas)
			throws FileNotFoundException, DocumentException, ParseException {

		estatus = "8,11,14";
		return viaticosUsUseCase.obtenerSolicitudesReporteDirector(estatus, empresas, fechaInicio, fechaFin);
	}

	@PostMapping("{empleado}/solicitudes-de-viaticos/")
	public Solicitud crearSolicitud(@PathVariable("empleado") String empleado, @RequestBody Solicitud solicitud) {
		return viaticosUsUseCase.crearSolicitud(empleado, solicitud);
	}

	@PutMapping("solicitudes-de-viaticos/{solicitud}/aprobacion")
	public void enviarAprobacionContador(@PathVariable("solicitud") int solicitud) {
		viaticosUsUseCase.enviaEstatusAprobacionContador(solicitud, 2);
	}

	@GetMapping("solicitudes-de-viaticos/{solicitud}")
	public Solicitud obtenerSolicitud(@PathVariable("solicitud") int solicitud) {
		return viaticosUsUseCase.obtenerSolicitudJPA(solicitud);
	}

	@DeleteMapping("solicitudes-de-viaticos/{solicitud}")
	public void eliminarSolicitud(@PathVariable("solicitud") int solicitud) {
		viaticosUsUseCase.eliminarSolicitud(solicitud);
	}

	@PutMapping("solicitudes-de-viaticos/{solicitud}")
	public void editarSolicitud(@PathVariable("solicitud") int solicitudId, @RequestBody Solicitud solicitud) {

		viaticosUsUseCase.editarSolicitud(solicitud);
	}

	@PostMapping("solicitudes-de-viaticos/{solicitud}/comprobantes")
	public void cargarComprobante(@PathVariable("solicitud") String solicitud,
			@RequestParam(value = "comprobante", required = false) String comprobante,
			@RequestParam(value = "xml", required = false) MultipartFile xml,
			@RequestParam(value = "pdf", required = false) MultipartFile pdf) {

		Gson gson = new Gson();
		Comprobante comprobanteJson = gson.fromJson(comprobante, Comprobante.class);
		viaticosUsUseCase.cargaDeComprobanteJPA(solicitud, comprobanteJson, xml, pdf);
	}

	@GetMapping("solicitudes-de-viaticos/{solicitud}/recalculo-no-aplica")
	public String recalculoNoAplica(@PathVariable("solicitud") int solicitud) {

		viaticosUsUseCase.recalculoNoAplica(solicitud);
		return "Se ejecuto correctamente el recalculo de la solicitud " + solicitud;
	}

	@GetMapping("solicitudes-de-viaticos/{solicitud}/recalculo-ish")
	public String recalculoIsh(@PathVariable("solicitud") int solicitud) {

		viaticosUsUseCase.recalculoISH(solicitud);
		return "Se ejecuto correctamente el recalculo de ISH " + solicitud;
	}

	@PostMapping("valida-cfdi")
	public void validaCfdi(@RequestParam(value = "xml", required = true) MultipartFile xml) {

		viaticosUsUseCase.validaCfdi(xml);
	}

	@GetMapping("comprobantes-de-viaticos/{comprobante}")
	public Comprobante obtenerComprobante(@PathVariable("comprobante") int idComprobante) {
		return viaticosUsUseCase.obtenerDeComprobante(idComprobante);
	}

	@PutMapping("comprobantes-de-viaticos/{idcomprobante}")
	public void modificarComprobante(@PathVariable("idcomprobante") int idComprobante,
			@RequestBody(required = false) Comprobante comprobante,
			@RequestParam(name = "aprobacion", required = false) String aprobacionAplica,
			@RequestParam(name = "usuario", required = false) String usuario) {
		viaticosUsUseCase.modificarComprobante(idComprobante, comprobante, aprobacionAplica, usuario);
	}
	
	@PutMapping("comprobantes-de-viaticos/monto-aprobado/{idcomprobante}")
	public void modificarComprobanteMontoADescontar(@PathVariable("idcomprobante") int idComprobante,
			@RequestParam(name = "montoAprobado", required = false) BigDecimal montoAprobado,
			@RequestParam(name = "usuario", required = false) String usuario) {
		viaticosUsUseCase.modificarMontoAprobado(idComprobante, montoAprobado, usuario);
	}

	@DeleteMapping("comprobantes-de-viaticos/{idComprobante}")
	public void eliminarComprobante(@PathVariable("idComprobante") int idComprobante) {
		viaticosUsUseCase.eliminarDeComprobante(idComprobante);
	}

	@PutMapping("solicitudes-de-viaticos/{solicitud}/comprobante-aprobacion")
	public void solicitarAprobacionDeSolicitudConComprobantes(@PathVariable("solicitud") int solicitud) {
		viaticosUsUseCase.solicitarViaticosParaUsuario(solicitud, 5, "");
	}

	@PutMapping("comprobantes-de-viaticos/{comprobante}/envia-aprobar")
	public void solicitarAprobacionDeComprobante(@PathVariable("comprobante") int comprobante) {
		viaticosUsUseCase.solicitaAprobacionDeComprobante(comprobante, "PENDIENTE DE APROBACION");
	}

	@GetMapping("solicitudes-de-viaticos/reporte")
	public List<Solicitud> solicitudesReporte(@RequestParam String fechaInicio, @RequestParam String fechaFin,
			@RequestParam String estatus, @RequestParam String empresas, @RequestParam String evento,
			@RequestParam int numeroSolicitud) throws FileNotFoundException, DocumentException, ParseException {
		log.info(estatus);
		log.info(empresas);
		log.info(fechaInicio);
		log.info(fechaFin);
		log.info(evento);
		log.info(""+numeroSolicitud);
		return viaticosUsUseCase.obtenerSolicitudesReporte(estatus, empresas, fechaInicio, fechaFin, evento,
				numeroSolicitud);
	}
	
	@GetMapping("solicitudes-de-viaticos/eventos-reporte")
	public List<String> eventosParaReporte()  {
		
		List<String> l = new ArrayList<>();
		l.addAll(ea.eventos());
		l.addAll(ea.eventosNo());
		return l;
	}

}
