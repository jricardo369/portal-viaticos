package com.viaticos.adapter.out.jobs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.ContabilizarFoxPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.JobFOXPort;
import com.viaticos.application.port.out.PeriodoPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Jobs;
import com.viaticos.domain.PrepolizaElementos;
import com.viaticos.domain.SolicitudViaticosEntity;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
@PropertySource(value = "classpath:configuraciones-fox.properties")
public class JobsAdapterFOX implements JobFOXPort {

	public static String estatusCargaEntrega = "Dispersión entrega";
	public static String estatusCargaComprobacion = "Comprobación";
	public static String estatusEnvioPoliza = "Envió de póliza";
	public static String estatusCancelada = "Cancelada";

	public static String eventoCargaEntrega = "EJECUCIÓN DE CARGA ENTREGA";
	public static String eventoCargaComprobacion = "EJECUCIÓN DE CARGA COMPROBACION";

	Logger log = LoggerFactory.getLogger(JobsAdapterFOX.class);

	@Value("${ambiente}")
	private String ambiente;

	@Value("${ruta.archivo.logjobs.general}")
	private String rutaArchivoLogGeneral;

	@Value("${fox-qas-php}")
	private String rutaPHPFoxQas;

	@Value("${fox-pro-php}")
	private String rutaPHPFoxPro;
	
	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Autowired
	private JobPort jobAdapter;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	public ArchivosPort archivosPort;

	@Autowired
	public PeriodoPort periodoPort;

	@Autowired
	private ContabilizarFoxPort contabilizarPort;

	StringBuilder logBuilder = null;

	@Override
	public String generarPolizaFox(int numeroSolicitud, boolean guardar) {

		List<Jobs> fox = new ArrayList<Jobs>();
		logBuilder = new StringBuilder();

		try {

			log.info("####################--- Inicio envio poliza "
					+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################");
			logBuilder.append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Se comienza el envio de poliza de comprobacion ");
			log.info("1. Obteniendo solicitudes");
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " 1. Obteniendo solicitudes");

			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;
			if (numeroSolicitud == 0) {
				solicitudes = solUsPort.encontrarSolicitudesPorEventoYSistema(estatusEnvioPoliza, "FOX");
			} else {
				solicitudes = new ArrayList<>();
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			log.info(" Solicitudes obtenidas:" + solicitudes.size());
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Solicitudes obtendidas " + solicitudes.size());

			if (!solicitudes.isEmpty()) {

				log.info("2. Se comenzará hacer barrido de solicitudes para enviar a FOX");
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " 2. Se comenzará hacer barrido de solicitudes para enviar a FOX");

				fox = envioAFox(solicitudes, guardar);

			}

			log.info("####################--- Fin envio poliza " + UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " ---####################" + "\n");
			logBuilder.append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Se finaliza el envio de poliza de comprobacion ");

		} catch (ParseException e) {
			e.printStackTrace();
			logBuilder.append("\n").append(UtilidadesAdapter.obtenerFechaYHoraActual() + " Error " + e.getMessage());
			jobAdapter.guardarLog(10, logBuilder);
		}

		RestTemplate rest = new RestTemplate();

		if (guardar) {
			// System.out.println(rutaPHPFoxQas);
			// System.out.println(rutaPHPFoxPro);
			String resp = "";
			if ("qas".equals(ambiente)) {
				resp = rest.postForObject(rutaPHPFoxQas + "rest.php", fox, String.class); // QAS
			} else if ("pro".equals(ambiente)) {
				resp = rest.postForObject(rutaPHPFoxPro + "rest.php", fox, String.class); // PRO
			}
			System.out.println(resp);
		}

		if (true) {
			jobAdapter.guardarLog(10, logBuilder);
		}

		// return fox;
		return logBuilder.toString();

	}

	private List<Jobs> envioAFox(List<SolicitudViaticosEntity> solicitudes, boolean guardar) throws ParseException {

		log.info("***** Enviando " + solicitudes.size() + " Solicitudes *****");

		int ultimoValor = 0;
		String texto = "";

		// Obtener ultimo valor de num poliza tipo 3 con el mes actual
		ultimoValor = contabilizarPort.obtenerUltimoValorCons();

		// Este metodo regresara una lista de jobs para poder insertar
		List<Jobs> jobs = new ArrayList<Jobs>();

		for (SolicitudViaticosEntity solicitud : solicitudes) {

			Jobs job = new Jobs();
			PrepolizaElementos prepolizaElementos = new PrepolizaElementos();

			ultimoValor++;

			// Solicitudes
			prepolizaElementos.setCabecera(contabilizarPort.insertarEncabezado(solicitud, ultimoValor));

			// Obtener comprobantes del objeto solicitud
			List<ComprobanteViaticoEntity> comprobantes = solicitud.getComprobanteViaticosEntity();
			// Insetar registro a fox de los comprobantes
			prepolizaElementos
					.setComprobantes(contabilizarPort.insertarComprobantes(comprobantes, solicitud, ultimoValor));

			// Insetar registro a fox de los nacionales en este caso del cfdi el cual viene
			// en el comprobante
			prepolizaElementos.setNacionales(contabilizarPort.insertarNacionales(comprobantes, solicitud, ultimoValor));

			// Insetar registro a fox de los doctos en este caso del cfdi el cual viene
			// en el comprobante
			prepolizaElementos.setDoctos(contabilizarPort.insertarDoctos(comprobantes, solicitud, ultimoValor));

			texto = "Núm póliza " + ultimoValor;
			if (guardar) {
				jobAdapter.agregarEvento(estatusEnvioPoliza, texto, solicitud);
				EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
				estatus = estatusPort.obtieneEstatusSolicitud(15);
				if (estatus == null)
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");
				solUsPort.enviaPeticionAceptacion(solicitud.getId(), estatus);
			}

			job.setPrepolizaElementos(prepolizaElementos);
			jobs.add(job);
		}

		log.info("***** Fin Solicitud *****");

		log.info("3. Se termina de formar los datos de estrucura de archivo");
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
				+ " 3. Se termina de formar los datos de estrucura de archivo");

		return jobs;
	}

	public static void main(String args[]) {

	}

	@Override
	public int tomarUltimoValorCabecera() {
		// Obtener ultimo valor de num poliza tipo 3 con el mes actual
		return contabilizarPort.obtenerUltimoValorCons();
	}

	@Override
	public String leerTabla(String tabla) {
		return contabilizarPort.leerTabla(tabla);
	}

	@Override
	public List<Jobs> generaPolizaFoxPhp() {

		PrepolizaElementos prepolizaElementos = new PrepolizaElementos();
		List<Jobs> jobs = new ArrayList<Jobs>();
		Jobs job = new Jobs();

		Object[] rowData = new Object[21];
		rowData[0] = "3"; // Tipo
		rowData[1] = "970";// Consecutivo
		Date d = UtilidadesAdapter.formatearDateaDate(new Date(), "dd/MM/yyyy");
		rowData[2] = d; // Fecha carga
		rowData[3] = 0; // Ref pol
		rowData[4] = ""; // Ban
		rowData[5] = ""; // Cta ori
		rowData[6] = ""; // Ban des
		rowData[7] = ""; // Cta des
		rowData[8] = UtilidadesAdapter.formatearDateaDate(new Date(), "dd/MM/yyyy"); // Fecha en que se entrega
		rowData[9] = " Prov liq gts " + "Humberto"; // Concepto
		rowData[10] = ""; // Uuid
		rowData[11] = false; // Ajuste
		rowData[12] = false; // LAuto
		rowData[13] = ""; // Tipo ref
		rowData[14] = 0; // Pol ref
		// int mes = Integer.valueOf(f.substring(5, 7));
		rowData[15] = 0; // Mes
		// int anio = Integer.valueOf(f.substring(0, 4));
		rowData[16] = 0; // Año
		rowData[17] = ""; // CPago
		rowData[18] = ""; // Cusuario
		rowData[19] = d; // Fecha mod
		rowData[20] = ""; // Hora mod

		prepolizaElementos.setCabecera(rowData);
		job.setPrepolizaElementos(prepolizaElementos);

		jobs.add(job);
		return jobs;
	}

}
