package com.viaticos.adapter.out.jobs;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.in.PrepolizaUseCase;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.JobSYS21Port;
import com.viaticos.application.port.out.PeriodoPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.SolicitudViaticosEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
@PropertySource(value = "classpath:configuraciones-sys21.properties")
public class JobsAdapterSYS21 implements JobSYS21Port {

	public static String estatusCargaEntrega = "Dispersión entrega";
	public static String estatusCargaComprobacion = "Comprobación";
	public static String estatusEnvioPoliza = "Envió de póliza";

	public static String eventoCargaEntrega = "EJECUCIÓN DE CARGA ENTREGA";
	public static String eventoCargaComprobacion = "EJECUCIÓN DE CARGA COMPROBACION";

	Logger log = LoggerFactory.getLogger(JobsAdapterSYS21.class);

	@Value("${ambiente}")
	private String ambiente;
	
	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Value("${ruta.servicio.sys21}")
	private String uriSys21;

	@Value("${ruta.servicio.sys21.qas}")
	private String uriSys21Qas;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	public ArchivosPort archivosPort;

	@Autowired
	public PeriodoPort periodoPort;

	@Autowired
	private EmpresaPort empPort;
	
	@Autowired
	private JobPort jobAdapter;

	@Autowired
	private PrepolizaUseCase poliza;

	StringBuilder logBuilder = null;

	boolean guardarLog = true;

	int periodo = 0;
	int referencia = 0;
	boolean sinPeriodo = false;

	

	
	@Override
	public String generarPolizaSys21(int numeroSolicitud,boolean guardar) {

		logBuilder = new StringBuilder();
		
		try {

			log.info("####################--- Inicio envio poliza "
					+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################");
			logBuilder.append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " Se comienza el envio de poliza a SYS21 ");
			log.info("1. Obteniendo solicitudes de SYS21");
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " 1. Obteniendo solicitudes de SYS21");

			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;
			if (numeroSolicitud == 0) {
				solicitudes = solUsPort.encontrarSolicitudesPorEventoYSistema("Envio de póliza", "SYS21");
			} else {
				solicitudes = new ArrayList<>();
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}
			
			log.info(" Solicitudes obtenidas de SYS21:" + solicitudes.size());
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Solicitudes obtendidas de SYS21" + solicitudes.size());

			if (!solicitudes.isEmpty()) {

				log.info("2. Se comenzará hacer barrido de solicitudes para enviar a SYS21");
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " 2. Se comenzará hacer barrido de solicitudes para enviar a SYS21");

				envioASys21(solicitudes,guardar);

			}

			log.info("####################--- Fin envio poliza " + UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " ---####################" + "\n");
			logBuilder.append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Se finaliza el envio de poliza de comprobacion ");

		} catch (ParseException e) {
			e.printStackTrace();
			logBuilder.append("\n").append(UtilidadesAdapter.obtenerFechaYHoraActual() + " Error " + e.getMessage());
			jobAdapter.guardarLog(10,logBuilder);
		}
		
		return logBuilder.toString();

	}

	private void envioASys21(List<SolicitudViaticosEntity> solicitudesSys21,boolean guardar) throws ParseException {

		JSONArray Array = new JSONArray();
		JSONObject jgeneral = new JSONObject();
		JSONObject jsonF = new JSONObject();
		JSONObject jprefijo = new JSONObject();

		String respuestaServicioSys21 = "";
		String texto = "";
		log.info("3. Inicia envió a SYS21");
		logBuilder.append("\n")
				.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " 3. Inicia envió a SYS21");

		for (SolicitudViaticosEntity solicitud : solicitudesSys21) {

			log.info("Empresa:" + solicitud.getEmpresa().trim() + " / " + "Ceco:" + solicitud.getCeco());
			EmpresaEntity rfc = empPort.obtenerEmpresaPorEmpresa(solicitud.getEmpresa().trim());
			boolean brfc = false;
			if (rfc == null) {
				log.info("No se encontro la empresa" + solicitud.getEmpresa());
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ "No se encontro la empresa" + solicitud.getEmpresa());

			} else {

				jprefijo.put("Bd", rfc.getBaseDeDatos());
				jsonF.put("Prefijo", jprefijo);
				brfc = true;

				if (brfc == true) {

					jgeneral.put("IdPolizaExterno", solicitud.getId());
					jgeneral.put("fechaPoliza", solicitud.getFechaCreacion());
					jgeneral.put("conceptoPoliza", solicitud.getConcepto());
					jsonF.put("PolizaGeneral", jgeneral);

					int IdSolicitud = solicitud.getId();

					List<Prepoliza> po = poliza.generarPoliza(IdSolicitud, false);
					log.info("Posiciones prepoliza: " + po.size());

					if (!po.isEmpty()) {

						for (Prepoliza p : po) {

							JSONObject jdetalle = new JSONObject();
							jdetalle.put("cuenta", p.getSubCuenta());
							jdetalle.put("concepto", p.getConcepto());

							jdetalle.put("cargo", p.getCargo());

							jdetalle.put("abono", p.getAbono());

							jdetalle.put("centrocosto", p.getCeco());

							Array.put(jdetalle);

						}
					}

					jsonF.put("PolizaDetalle", Array);
					String JSONEnvio = jsonF.toString();
					log.info(JSONEnvio);
					respuestaServicioSys21 = envioServicioSys21(JSONEnvio);
					log.info("Resp envio sys21:" + respuestaServicioSys21);
					if (!respuestaServicioSys21.isEmpty()) {
						if (!respuestaServicioSys21.substring(0, 3).equals("500")) {
							texto = "Núm póliza " + respuestaServicioSys21;
							if (guardar) {
								jobAdapter.agregarEvento(estatusEnvioPoliza, texto, solicitud);
								EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
								estatus = estatusPort.obtieneEstatusSolicitud(15);
								if (estatus == null)
									throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");
								solUsPort.enviaPeticionAceptacion(solicitud.getId(), estatus);
							}
						} else {
							log.info(
									"Ocurrio un error en la respuesta del servicio de SYS21 al enviar la poliza de la solicitud "
											+ solicitud.getId());
							logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
									+ " Ocurrio un error en la respuesta del servicio de SYS21 al enviar la poliza de la solicitud "
									+ solicitud.getId());
						}
					}

				} else {
					log.info("No se encontro relacion con la empresa" + rfc.getCodigo_empresa());
					logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
							+ " No se encontro relacion con la empresa" + rfc.getCodigo_empresa());

				}

			}

		}

		log.info("4. Finaliza envió a SYS21");
		logBuilder.append("\n")
				.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " 4. Finaliza envió a SYS21");
		if (guardar) {
			jobAdapter.guardarLog(11, logBuilder);
		}

		return;
	}

	public String envioServicioSys21(String JSONEnvio) {

		String salida = "";

		try {

			String ruta = "";
			if ("qas".equals(ambiente)) {
				ruta = uriSys21Qas; // QAS
			} else if ("pro".equals(ambiente)) {
				ruta = uriSys21; // PRO
			}
			
			log.info("Ruta:" + ruta);
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + "Ruta:" + ruta);
			log.info("JSONEnvio:" + JSONEnvio);

			URL url = new URL(ruta);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = JSONEnvio.getBytes("utf-8");
				os.write(input, 0, input.length);
			}
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				salida = response.toString();
			}
		} catch (MalformedURLException e) {
			salida = "500";
			log.info("Error:" + salida + "/" + e.getMessage());
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " Error:" + salida + "/" + e.getMessage());
			log.info("JSONEnvio:" + JSONEnvio);
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " JSONEnvio:" + JSONEnvio);
			return salida;
		} catch (IOException e) {
			salida = "500";
			salida = "500";
			log.info("Error:" + salida + "/" + e.getMessage());
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " Error:" + salida + "/" + e.getMessage());
			log.info("JSONEnvio:" + JSONEnvio);
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " JSONEnvio:" + JSONEnvio);
			return salida;
		}

		try {
			log.info("Salida envio:" + salida);
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " Salida envio:" + salida);
			JSONObject object = new JSONObject(salida);
			JSONObject objectS = new JSONObject(object.get("Exito").toString());
			salida = objectS.get("IdPoliza").toString();
		} catch (JSONException e) {
			JSONObject object = new JSONObject(salida);
			JSONObject objectS = new JSONObject(object.get("Error").toString());
			salida = objectS.get("Mensaje").toString();
			salida = "500-JSON de respuesta incorrecto";
			log.info("Error:" + salida + "/" + e.getMessage());
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " Error:" + salida + "/" + e.getMessage());
			log.info("JSONEnvio:" + JSONEnvio);
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " JSONEnvio:" + JSONEnvio);
			return salida;
		}

		return salida;

	}

	
	public static void main(String args[]) {

	}

}
