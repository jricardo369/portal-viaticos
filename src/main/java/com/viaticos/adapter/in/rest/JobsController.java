package com.viaticos.adapter.in.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viaticos.application.port.out.JobFOXPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.JobSAPB1Port;
import com.viaticos.application.port.out.JobSYS21Port;

@RequestMapping("/jobs")
@RestController
public class JobsController {

	@Autowired
	private JobPort jobPort;

	@Autowired
	private JobSAPB1Port jobSapB1Port;

	@Autowired
	private JobSYS21Port jobSYS21Port;
	
	@Autowired
	private JobFOXPort jobFOXPort;

	@GetMapping("carga-entrega/{fecha}")
	public void generarLayoutCargaEntrega(@PathVariable("fecha") String fecha) {
		jobPort.generarLayoutCargaEntrega(fecha, true, 0);
	}

	@GetMapping("carga-entrega-sg/{fecha}")
	public String generarLayoutCargaEntregaSG(@PathVariable("fecha") String fecha) {
		return jobPort.generarLayoutCargaEntrega(fecha, false, 0);
	}

	@GetMapping("carga-comprobacion/{fecha}")
	public void generarLayoutCargaComprobacion(@PathVariable("fecha") String fecha) {
		jobPort.generarLayoutCargaComprobacion(fecha, true, 0);
	}

	@GetMapping("carga-comprobacion-sg/{fecha}")
	public String generarLayoutCargaComprobacionSG(@PathVariable("fecha") String fecha) {
		return jobPort.generarLayoutCargaComprobacion(fecha, false, 0);
	}

	@GetMapping("layout/{tipo}/{fecha}/{numeroSolicitud}")
	public String generarLayoutDeTipo(@PathVariable("tipo") String tipo, @PathVariable("fecha") String fecha,
			@PathVariable("numeroSolicitud") int numeroSolicitud) {
		if (tipo.equals("dispersion")) {
			return jobPort.generarLayoutCargaEntrega(fecha, false, numeroSolicitud);
		}
		if (tipo.equals("comprobacion")) {
			return jobPort.generarLayoutCargaComprobacion(fecha, false, numeroSolicitud);
		}
		return "";

	}

	@GetMapping("poliza-fox")
	public void generarLPolizaFox() {
		jobFOXPort.generarPolizaFox(0, true);
	}

	@GetMapping("poliza-fox-sg/{numeroSolicitud}/{guardar}")
	public String generarLPolizaFoxSG(@PathVariable("numeroSolicitud") int numeroSolicitud,
			@PathVariable("guardar") boolean guardar) {
		return jobFOXPort.generarPolizaFox(numeroSolicitud, guardar);
	}

	@GetMapping("poliza-sys21")
	public void generarLPolizaSys21() {
		jobSYS21Port.generarPolizaSys21(0, true);
	}

	@GetMapping("poliza-sys21-sg/{numeroSolicitud}/{guardar}")
	public String generarLPolizaSys21SG(@PathVariable("numeroSolicitud") int numeroSolicitud,
			@PathVariable("guardar") boolean guardar) {
		return jobSYS21Port.generarPolizaSys21(numeroSolicitud, guardar);
	}

	@GetMapping("poliza-sapb1")
	public void generarLPolizaSapB1() {
		jobSapB1Port.generarPolizaSAPB1(0, true);
	}

	@GetMapping("poliza-sapb1-sg/{numeroSolicitud}/{guardar}")
	public String generarLPolizaSapB1SG(@PathVariable("numeroSolicitud") int numeroSolicitud,
			@PathVariable("guardar") boolean guardar) {
		return jobSapB1Port.generarPolizaSAPB1(numeroSolicitud, guardar);
	}

	@GetMapping("ultimo-valor-cab")
	public int obtenerUltimoValor() {
		return jobFOXPort.tomarUltimoValorCabecera();
	}

	@GetMapping("lee-tabla/{tabla}")
	public String obtenerUltimoValor(@PathVariable("tabla") String tabla) {
		return jobFOXPort.leerTabla(tabla);
	}

	@GetMapping("limpiar-solicitudes-fuera-rango-fechas/{numeroSolicitud}/{guardar}")
	public String limpiarSolicitudesFueraRango(@PathVariable("numeroSolicitud") int numeroSolicitud,
			@PathVariable("guardar") boolean guardar) {
		return jobPort.limpiarSolicitudesFueraRango(numeroSolicitud, guardar);
	}

}
