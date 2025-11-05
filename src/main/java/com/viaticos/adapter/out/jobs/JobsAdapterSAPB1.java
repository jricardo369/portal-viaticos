package com.viaticos.adapter.out.jobs;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.in.PrepolizaUseCase;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.JobSAPB1Port;
import com.viaticos.application.port.out.PeriodoPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.SubCuentasContablesPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.Respuesta;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.SubCuentaContableEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
@PropertySource(value = "classpath:configuraciones-sap-b1.properties")
public class JobsAdapterSAPB1 implements JobSAPB1Port {

	public static String estatusCargaEntrega = "Dispersión entrega";
	public static String estatusCargaComprobacion = "Comprobación";
	public static String estatusEnvioPoliza = "Envió de póliza";

	public static String eventoCargaEntrega = "EJECUCIÓN DE CARGA ENTREGA";
	public static String eventoCargaComprobacion = "EJECUCIÓN DE CARGA COMPROBACION";

	Logger log = LoggerFactory.getLogger(JobsAdapterSAPB1.class);

	@Value("${ambiente}")
	private String ambiente;

	@Value("${ruta.servicio.sapb1}")
	private String uriServerSABB1;

	@Value("${ruta.servicio.sapb1.qas}")
	private String uriServerSABB1Qas;

	@Autowired
	private JobPort jobAdapter;

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	public ArchivosPort archivosPort;

	@Autowired
	public PeriodoPort periodoPort;

	@Autowired
	private EmpresaPort empPort;

	@Autowired
	private PrepolizaUseCase poliza;

	@Autowired
	private SubCuentasContablesPort subCuentaPort;

	StringBuilder logBuilder = null;

	boolean guardarLog = true;

	int periodo = 0;
	int referencia = 0;
	boolean sinPeriodo = false;

	@Override
	public String generarPolizaSAPB1(int numeroSolicitud, boolean guardar) {

		logBuilder = new StringBuilder();

		try {

			pintarEnlog(logBuilder, "#################### Inicio envio polizas a SAPB1 ");
			pintarEnlog(logBuilder, "1. Obteniendo solicitudes de SAPB1");

			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;
			if (numeroSolicitud == 0) {
				solicitudes = solUsPort.encontrarSolicitudesPorEventoYSistema("Envio de póliza", "SAPB1");
			} else {
				solicitudes = new ArrayList<>();
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			pintarEnlog(logBuilder, "Solicitudes obtenidas de SAPB1:" + solicitudes.size());

			if (!solicitudes.isEmpty()) {

				pintarEnlog(logBuilder, "2. Se comenzará hacer barrido de solicitudes para enviar a SAPB1");
				envioPolizaASAPB1(solicitudes, guardar);

			}

			pintarEnlog(logBuilder, "####################--- Fin envio poliza SAPB1");

		} catch (ParseException e) {
			e.printStackTrace();
			pintarEnlog(logBuilder, "Error " + e.getMessage());
			jobAdapter.guardarLog(10, logBuilder);
		}

		return logBuilder.toString();

	}

	private void envioPolizaASAPB1(List<SolicitudViaticosEntity> solicitudes, boolean guardar) throws ParseException {

		JSONArray Array = new JSONArray();
		JSONObject jsonDatos = null;
		JSONObject jsonFormado = new JSONObject();
		JSONArray jsonFormadoArray = new JSONArray();
		JSONArray jsonArraySalida = new JSONArray();
		EmpresaEntity empresaObjAnterior = null;

		String empresaVal = "";
		String empresaAnterior = "";
		int count = 1;
		int tamanio = solicitudes.size();

		List<Respuesta> respuestaServicioASAPB1 = null;
		String texto = "";
		pintarEnlog(logBuilder, "3. Inicia envió a SAPB1 ");

		for (SolicitudViaticosEntity solicitud : solicitudes) {

			log.info("Empresa:" + solicitud.getEmpresa().trim() + " / " + "Ceco:" + solicitud.getCeco());
			EmpresaEntity empresa = empPort.obtenerEmpresaPorEmpresa(solicitud.getEmpresa().trim());

			if (empresa == null) {

				pintarEnlog(logBuilder, "No se encontro la empresa" + solicitud.getEmpresa());

			} else {

				if ("SAPB1".equals(empresa.getSistema())) {

					jsonDatos = new JSONObject();
					jsonFormado = new JSONObject();
					empresaVal = empresa.getCodigo_empresa();

					if (count == 1) {
						empresaAnterior = empresaVal;
						empresaObjAnterior = empresa;
					}

					if (empresaAnterior.equals(empresaVal)) {

						if (count == 1) {
							pintarEnlog(logBuilder, "Comienza calculo de solicitudes de empresa " + empresaVal);
							jsonDatos = new JSONObject();
						} else {
							pintarEnlog(logBuilder, "Continua calculo de solicitudes de empresa" + empresaVal);
						}

					} else {

						// Tomar datos de conexion
						String datosConexion = empresaObjAnterior.getDatosConexion();
						JSONObject jgeneral = new JSONObject();
						// Agregar datos conexion a objeto jgenreal
						datosConexion(jgeneral, empresaAnterior, datosConexion, logBuilder);
						// Agregar dispersiones a jgeneral
						jgeneral.put("polizas", jsonFormadoArray);

						// Crear objeto de salida
						jsonArraySalida.put(jgeneral);

						pintarEnlog(logBuilder, "Empresa cambio " + empresaVal);
						jsonDatos = new JSONObject();
						jsonFormado = new JSONObject();
						jsonFormadoArray = new JSONArray();

					}

					empresaAnterior = empresaVal;
					empresaObjAnterior = empresa;
					count++;

					jsonDatos.put("numeroSolicitud", solicitud.getId());
					jsonDatos.put("empresa", empresaVal);
					jsonDatos.put("codigoUsuario", solicitud.getUsuario());
					jsonDatos.put("nombreUsuario", solicitud.getNombreCompletoUsuario());
					jsonDatos.put("rfcUsuario", solicitud.getRfc());
					jsonDatos.put("fechaPoliza", solicitud.getFechaCreacion());
					jsonDatos.put("conceptoPoliza", solicitud.getConcepto());
					jsonDatos.put("montoAnticipo", solicitud.getAnticipo());
					jsonDatos.put("montoComprobado", solicitud.getAnticipo());
					jsonDatos.put("archivos", "ruta/archivos.zip");
					jsonDatos.put("descripcionPoliza",
							"Comprobación de Viaticos – " + solicitud.getNombreCompletoUsuario());
					jsonDatos.put("descripcionPago", "Aplicación Viáticos – " + solicitud.getNombreCompletoUsuario());
					jsonDatos.put("centroCosto", solicitud.getCeco());

					jsonFormado.put("polizaGeneral", jsonDatos);

					int IdSolicitud = solicitud.getId();

					List<Prepoliza> po = poliza.generarPoliza(IdSolicitud, false);
					log.info("Posiciones prepoliza: " + po.size());

					if (!po.isEmpty()) {

						for (Prepoliza p : po) {

							JSONObject jdetalle = new JSONObject();
							jdetalle.put("fecha", p.getFecha());
							jdetalle.put("subCuenta", p.getSubCuenta());
							jdetalle.put("concepto", p.getConcepto());
							jdetalle.put("uuid", p.getUuid());
							jdetalle.put("rfc", p.getRfc());
							jdetalle.put("cargo", p.getCargo());
							jdetalle.put("abono", p.getAbono());
							jdetalle.put("tipoGasto", p.getTipo());
							jdetalle.put("tipo", p.getTipo());
							Array.put(jdetalle);

						}
					}

					jsonFormado.put("polizaDetalle", Array);
					jsonFormadoArray.put(jsonFormado);

					if (tamanio == count - 1) {

						// Tomar datos de conexion
						String datosConexion = empresaObjAnterior.getDatosConexion();
						JSONObject jgeneral = new JSONObject();
						// Agregar datos conexion a objeto jgenreal
						datosConexion(jgeneral, empresaAnterior, datosConexion, logBuilder);
						// Agregar dispersiones a jgeneral
						jgeneral.put("polizas", jsonFormadoArray);
						// Crear objeto de salida
						jsonArraySalida.put(jgeneral);

					}
				}

			}
		}

		pintarEnlog(logBuilder, "Empresas a la que se le enviaran polizas: " + jsonArraySalida.length());

		// Se enviara a SAP B1
		String JSONEnvio = jsonArraySalida.toString();
		respuestaServicioASAPB1 = envioServicioASAPB1(JSONEnvio, logBuilder);
		log.info("Resp envio SAP B1:" + respuestaServicioASAPB1);
		if (!respuestaServicioASAPB1.isEmpty()) {
			for (Respuesta respuesta : respuestaServicioASAPB1) {
				if (respuesta.getEstatus() == 0) {
					// Formando texto para numero de poliza
					texto = "Núm póliza " + respuesta.getFicha();
					String numSol = respuesta.getMensaje();
					// Buscando numero solicitud en el listado de solicitudes
					SolicitudViaticosEntity s = solicitudes.stream()
							.filter(sol -> numSol.equals(String.valueOf(sol.getId()))).findAny().orElse(null);
					// Pintando solicitud encontrada
					if (s != null) {
						log.info("solicitud encontrada en listado:" + s.getId());
						// Agregar evento a solicitud
						jobAdapter.agregarEvento(estatusEnvioPoliza, texto, s);
					} else {
						pintarEnlog(logBuilder, "No se encontro solicitud en el listado: " + numSol);
					}
					if (guardar) {
						// Cambiando estatus a solicitud
						EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
						estatus = estatusPort.obtieneEstatusSolicitud(15);
						if (estatus == null)
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");
						solUsPort.enviaPeticionAceptacion(Integer.valueOf(numSol), estatus);
						// Enviar archivos a Servidor SAP
						envioArchivosASAP(s.getComprobanteViaticosEntity());
					}
				} else {
					pintarEnlog(logBuilder,
							"Ocurrio un error en la respuesta del servicio de SYS21 al enviar la poliza de la solicitud "
									+ respuesta.getFicha());
				}
			}

		}

		pintarEnlog(logBuilder, "4. Finaliza envió a SAPB1");
		if (guardar) {
			jobAdapter.guardarLog(12, logBuilder);
		}

		return;
	}

	private boolean envioArchivosASAP(List<ComprobanteViaticoEntity> comprobantes) {
		boolean envioCorrecto = true;
		@SuppressWarnings("unused")
		byte[] archivos = archivosPort.obtenerArchivosSolicitudZip(comprobantes);
		// Metodo para enviar archivos a servidor SAP B1
		return envioCorrecto;
	}

	@Override
	public void envioDispercionASAPB1(List<SolicitudViaticosEntity> solicitudes, boolean guardar,
			StringBuilder logBuilder) {

		JSONObject jsonDispersion = null;
		JSONArray jsonArrayDispersion = null;
		JSONArray jsonArraySalida = new JSONArray();

		EmpresaEntity empresaObjAnterior = null;
		SolicitudViaticosEntity solicitudAnterior = null;

		String empresaVal = "";
		String empresaAnterior = "";
		String usuarioVal = "";
		String usuarioAnterior = "";
		String ceco = "";
		String emp = "";

		StringBuilder sols = new StringBuilder();

		BigDecimal sumaAnticipo = BigDecimal.ZERO;

		int count = 1;
		int tamanio = solicitudes.size();

		List<Respuesta> respuestaServicioASAPB1 = null;
		pintarEnlog(logBuilder, "3. Inicia envió dispersion a SAPB1 ");

		for (SolicitudViaticosEntity solicitud : solicitudes) {

			pintarEnlog(logBuilder,"************************* Solicitud:" + solicitud.getId());
			pintarEnlog(logBuilder,"Empresa:" + solicitud.getEmpresa().trim() + " / " + "Ceco:" + solicitud.getCeco());
			EmpresaEntity empresa = empPort.obtenerEmpresaPorEmpresa(solicitud.getEmpresa().trim());

			if (empresa == null) {
				pintarEnlog(logBuilder, "No se encontro la empresa" + solicitud.getEmpresa());
			} else {

				if ("SAPB1".equals(empresa.getSistema())) {

					jsonDispersion = new JSONObject();
					empresaVal = empresa.getCodigo_empresa();
					usuarioVal = solicitud.getUsuario();
					
					ceco = solicitud.getCeco().trim();
					emp = empresa.getEmpresa();
					pintarEnlog(logBuilder, "RFC empresa solicitud:" + emp);

					if (count == 1) {
						empresaAnterior = empresaVal;
						empresaObjAnterior = empresa;
						usuarioAnterior = usuarioVal;
					}

						if (empresaAnterior.equals(empresaVal)) {

							if (count == 1) {
								pintarEnlog(logBuilder, "Comienza calculo de solicitudes de empresa " + empresaVal);
								jsonArrayDispersion = new JSONArray();
							} else {
								pintarEnlog(logBuilder, "Continua calculo de solicitudes de empresa " + empresaVal);
							}

						} else {

							// Tomar datos de conexion
							String datosConexion = empresaObjAnterior.getDatosConexion();
							JSONObject jgeneral = new JSONObject();
							// Agregar datos conexion a objeto jgenreal
							datosConexion(jgeneral, empresaAnterior, datosConexion, logBuilder);
							// Agregar dispersiones a jgeneral
							jgeneral.put("dispersion", jsonArrayDispersion);

							// Crear objeto de salida
							jsonArraySalida.put(jgeneral);
							pintarEnlog(logBuilder,"Empresa cambio " + empresaVal);
							jsonArrayDispersion = new JSONArray();

						}

						empresaAnterior = empresaVal;
						empresaObjAnterior = empresa;

						// Validar si es el mismo usuario, en caso de ser igual ir sumando anticipos de
						// todas sus soliciutdes

						if (usuarioAnterior.equals(usuarioVal)) {

							sumaAnticipo = sumaAnticipo.add(solicitud.getAnticipo());
							solicitudAnterior = solicitud;
							sols.append(solicitud.getId() + ",");
							if (count == 1) {
								pintarEnlog(logBuilder, "Comienza calculo de usuario " + usuarioVal);
							} else {
								pintarEnlog(logBuilder, "Continua calculo de usuario " + usuarioVal);
							}

						} else {

							jsonDispersion.put("descripcion",
									"VIÁTICOS " + solicitudAnterior.getUsuario() +"-"+ solicitudAnterior.getNombreCompletoUsuario());
							jsonDispersion.put("monto", sumaAnticipo);
							jsonDispersion.put("moneda", "MXP");
							jsonDispersion.put("cuentaColaborador", solicitudAnterior.getCuentaContable());
							jsonDispersion.put("nombreColaborador", solicitudAnterior.getNombreCompletoUsuario());
							jsonDispersion.put("noFicha", sols.substring(0, sols.length() - 1));

							SubCuentaContableEntity subCuentaBanco = subCuentaPort.obtenerSubCuentaPorTipo(emp, ceco,
									"BANCOS");
							if (subCuentaBanco == null) {

								pintarEnlog(logBuilder,
										"No se encuentra configurada la subcuenta de BANCOS para la empresa " + emp
												+ " y ceco " + ceco);
							} else {

								jsonDispersion.put("cuentaContable", subCuentaBanco.getCodigo());
								jsonArrayDispersion.put(jsonDispersion);

							}

							sumaAnticipo = BigDecimal.ZERO;
							solicitudAnterior = solicitud;
							sols = new StringBuilder();

							sumaAnticipo = sumaAnticipo.add(solicitudAnterior.getAnticipo());
							sols.append(solicitudAnterior.getId() + ",");

							pintarEnlog(logBuilder, "Cambio usuario " + usuarioVal);

						}
						usuarioAnterior = usuarioVal;

						count++;

						if (tamanio == count - 1) {

							jsonDispersion = new JSONObject();
							// Agregar el ultimo
							jsonDispersion.put("descripcion",
									"VIÁTICOS " + solicitudAnterior.getNombreCompletoUsuario());
							jsonDispersion.put("monto", sumaAnticipo);
							jsonDispersion.put("moneda", "MXP");
							jsonDispersion.put("cuentaColaborador", solicitudAnterior.getCuentaContable());
							jsonDispersion.put("nombreColaborador", solicitudAnterior.getNombreCompletoUsuario());
							jsonDispersion.put("noFicha", sols.substring(0, sols.length() - 1));
							SubCuentaContableEntity subCuentaBanco = subCuentaPort.obtenerSubCuentaPorTipo(emp, ceco,
									"BANCOS");
							if (subCuentaBanco == null) {

								pintarEnlog(logBuilder,
										"No se encuentra configurada la subcuenta de BANCOS para la empresa " + emp
												+ " y ceco " + ceco);
							} else {

								jsonDispersion.put("cuentaContable", subCuentaBanco.getCodigo());
								jsonArrayDispersion.put(jsonDispersion);

							}
							pintarEnlog(logBuilder, "Ultima solicitud  " + solicitudAnterior.getId());

							// Tomar datos de conexion
							String datosConexion = empresaObjAnterior.getDatosConexion();
							JSONObject jgeneral = new JSONObject();
							// Agregar datos conexion a objeto jgenreal
							datosConexion(jgeneral, empresaAnterior, datosConexion, logBuilder);
							// Agregar dispersiones a jgeneral
							jgeneral.put("dispersion", jsonArrayDispersion);
							// Crear objeto de salida
							jsonArraySalida.put(jgeneral);

						}

					

				}

			}

		}

		// Se enviara a SAP B1
		String JSONEnvio = jsonArraySalida.toString();
		log.info(JSONEnvio);
		respuestaServicioASAPB1 = envioServicioASAPB1(JSONEnvio, logBuilder);
		pintarEnlog(logBuilder, "Resp envio SAP B1:" + respuestaServicioASAPB1);
		if (!respuestaServicioASAPB1.isEmpty()) {
			for (Respuesta respuesta : respuestaServicioASAPB1) {
				if (respuesta.getEstatus() == 0) {
					// Tomando num solicitud
					String numsSol = respuesta.getFicha();
					// Tomando numero de documento generado
					String docNum = respuesta.getDocNum();
					pintarEnlog(logBuilder, "Numero de solicitud:" + numsSol);
					// Tomar los valores separados por coma
					List<String> solsFin = Arrays.asList(numsSol.split(","));
					// Barrido de las solicitudes encontradas
					for (String s : solsFin) {
						pintarEnlog(logBuilder, "NS:" + s);
						// Buscando numero solicitud en el listado de solicitudes
						SolicitudViaticosEntity se = solicitudes.stream()
								.filter(sol -> s.equals(String.valueOf(sol.getId()))).findAny().orElse(null);
						// Pintando solicitud encontrada
						if (se != null) {
							log.info("solicitud encontrada en listado:" + se.getId());
							if (guardar) {
								// Agregar evento a solicitud
								jobAdapter.agregarEvento(estatusCargaEntrega, "SAPB1 doc. num. " + docNum, se);
							}
						} else {
							pintarEnlog(logBuilder, "No se encontro solicitud en el listado");
						}
					}

				} else {
					pintarEnlog(logBuilder,
							"Ocurrio un error en la respuesta del servicio de SAPB1 al enviar la poliza de la solicitud "
									+ respuesta.getFicha());
				}
			}

		}
		pintarEnlog(logBuilder, "4. Finaliza envió a SAPB1");
		if (guardar) {
			jobAdapter.guardarLog(12, logBuilder);
		}

		return;
	}

	public void datosConexion(JSONObject jsonF, String empresa, String datosConexion, StringBuilder logBuilder) {

//		datosConexion = "server=SERV01;" + "licenseServer=SERV01:2000;" + "companyDB=12AM12;" + "userName=usertest;"
//				+ "password=pass;" + "dbUserName=user;" + "dbPassword=pass;" + "dbServerType=MSSQL2014;"
//				+ "language=ln_Spanish_La;" + "useTrusted=false;";

		log.info("datos conexion:" + datosConexion);

		String server = StringUtils.substringBetween(datosConexion, "server=", ";");
		String licenseServer = StringUtils.substringBetween(datosConexion, "licenseServer=", ";");
		String companyDB = StringUtils.substringBetween(datosConexion, "companyDB=", ";");
		String userName = StringUtils.substringBetween(datosConexion, "userName=", ";");
		String password = StringUtils.substringBetween(datosConexion, "password=", ";");
		String dbUserName = StringUtils.substringBetween(datosConexion, "dbUserName=", ";");
		String dbPassword = StringUtils.substringBetween(datosConexion, "dbPassword=", ";");
		String dbServerType = StringUtils.substringBetween(datosConexion, "dbServerType=", ";");
		String language = StringUtils.substringBetween(datosConexion, "language=", ";");
		String useTrusted = StringUtils.substringBetween(datosConexion, "useTrusted=", ";");

		// log.info("##### server:"+server);
		// log.info("##### licenseServer:"+licenseServer);
		// log.info("##### companyDB:"+companyDB);
		// log.info("##### userName:"+userName);
		// log.info("##### password:"+password);
		// log.info("##### dbUserName:"+dbUserName);
		// log.info("##### dbPassword:"+dbPassword);
		// log.info("##### dbServerType:"+dbServerType);
		// log.info("##### language:"+language);
		// log.info("##### useTrusted:"+useTrusted);

		String sNull = "null";
		if (sNull.equals(server) || sNull.equals(licenseServer) || sNull.equals(companyDB) || sNull.equals(userName)
				|| sNull.equals(password) || sNull.equals(dbUserName) || sNull.equals(dbPassword)
				|| sNull.equals(dbServerType) || sNull.equals(language) || sNull.equals(useTrusted)) {
			pintarEnlog(logBuilder, "Revisa que los datos de conexion esten llenos para la empresa " + empresa);

		} else {

			String pass = password;
			String bdpass = dbPassword;
			String passInB64 = Base64.getEncoder().encodeToString(pass.getBytes());
			String bdpassInB64 = Base64.getEncoder().encodeToString(bdpass.getBytes());
			JSONObject jconexion = new JSONObject();

			jconexion.put("empresa", empresa);
			jconexion.put("server", server);
			jconexion.put("licenseServer", licenseServer);
			jconexion.put("companyDB ", companyDB);
			jconexion.put("vUserName", userName);
			jconexion.put("vPs", passInB64);
			jconexion.put("dbUserName", dbUserName);
			jconexion.put("dbPs", bdpassInB64);
			jconexion.put("dbServerType", dbServerType);
			jconexion.put("language", language);
			jconexion.put("useTrusted", useTrusted);
			jsonF.put("conexion", jconexion);
		}
	}

	public List<Respuesta> envioServicioASAPB1(String JSONEnvio, StringBuilder logBuilder) {

		String salida = "";
		List<Respuesta> respuesta = null;
		pintarEnlog(logBuilder, " JSON a enviar:" + JSONEnvio);

		try {

			String ruta = "";
			if ("qas".equals(ambiente)) {
				ruta = uriServerSABB1Qas; // QAS
			} else if ("pro".equals(ambiente)) {
				ruta = uriServerSABB1; // PRO
			}

			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " Se enviará a endpoint:" + ruta);

			URL url = new URL(ruta);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
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
			pintarEnlog(logBuilder, " Error:" + salida + "/" + e.getMessage());
			pintarEnlog(logBuilder, "JSONEnvio:" + JSONEnvio);
			return formarRespuesta(salida);

		} catch (IOException e) {
			pintarEnlog(logBuilder, " Error:" + salida + "/" + e.getMessage());
			pintarEnlog(logBuilder, "JSONEnvio:" + JSONEnvio);
			return formarRespuesta(salida);
		}

		try {
			pintarEnlog(logBuilder, "Salida envio:" + salida);

			Gson gson = new Gson();
			Type ListType = new TypeToken<ArrayList<Respuesta>>() {
			}.getType();

			respuesta = gson.fromJson(salida, ListType);

		} catch (JSONException e) {
			JSONObject object = new JSONObject(salida);
			JSONObject objectS = new JSONObject(object.get("Error").toString());
			salida = objectS.get("Mensaje").toString();
			salida = "500-JSON de respuesta incorrecto";
			pintarEnlog(logBuilder, " Error:" + salida + "/" + e.getMessage());
			pintarEnlog(logBuilder, "JSONEnvio:" + JSONEnvio);
			return formarRespuesta(salida);
		}

		return respuesta;

	}

	public List<Respuesta> formarRespuesta(String error) {
		List<Respuesta> salida = new ArrayList<>();
		Respuesta r = new Respuesta();
		r.setEstatus(1);
		r.setMensaje(error);
		return salida;
	}

	public void pintarEnlog(StringBuilder logBuilder, String datos) {
		log.info(datos);
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " " + datos);
	}

	public static void main(String args[]) {

	}

}
