package com.viaticos.adapter.out.jobs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.Conceptos;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.EventoConfiguracionPort;
import com.viaticos.application.port.out.EventoDeViaticoPort;
import com.viaticos.application.port.out.JobPort;
import com.viaticos.application.port.out.PeriodoPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.ConfiguracionEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.layoutComprobacion;

import static java.util.stream.Collectors.*;

@Service
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
@PropertySource(value = "classpath:configuraciones-fox.properties")
public class JobsAdapter implements JobPort {

	public static String estatusCargaEntrega = "Dispersión entrega";
	public static String estatusCargaComprobacion = "Comprobación";
	public static String estatusEnvioPoliza = "Envió de póliza";

	public static String eventoSolicitudFueraRango = "Solicitud fecha fuera rango";
	public static String eventoCargaEntrega = "EJECUCIÓN DE CARGA ENTREGA";
	public static String eventoCargaComprobacion = "EJECUCIÓN DE CARGA COMPROBACION";

	Logger log = LoggerFactory.getLogger(JobsAdapter.class);

	@Value("${ambiente}")
	private String ambiente;

	@Autowired
	private EstatusSolicitudPort estatusPort;

	@Value("${numero.carga.entrega}")
	private int numCE;

	@Value("${numero.carga.comprobaciones}")
	private int numCC;

	@Value("${ruta.archivo.ce.nombre}")
	private String archivoCeNombre;

	@Value("${ruta.archivo.cc.nombre}")
	private String archivocCcNombre;

	@Value("${ruta.archivo.logjobs.general}")
	private String rutaArchivoLogGeneral;

	@Value("${ruta.archivo.logjobs.cargaentrega}")
	private String rutaArchivoLogEntrega;

	@Value("${ruta.archivo.logjobs.cargacomprobacion}")
	private String rutaArchivoLogComprobacion;

	@Value("${ruta.archivo.logjobs.cargafox}")
	private String rutaArchivoLogFox;

	@Value("${ruta.archivo.logjobs.cargasys21}")
	private String rutaArchivoLogSys21;

	@Value("${ruta.archivo.logjobs.cargasSABB1}")
	private String rutaArchivoLogSABB1;

	@Autowired
	private EventoConfiguracionPort eventoPort;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	public ArchivosPort archivosPort;

	@Autowired
	public PeriodoPort periodoPort;

	@Autowired
	private ConfiguracionesPort confPort;

	@Autowired
	private EventoDeViaticoPort eventoDeViaticosPort;

	@Autowired
	private JobsAdapterSAPB1 jobsSABB1;

	@Autowired
	private Conceptos concepto;

	@Autowired
	private CfdiPort cfdiPort;

	StringBuilder logBuilder = null;

	boolean guardarLog = true;

	int periodo = 0;
	int referencia = 0;
	boolean sinPeriodo = false;

	@Override
	public String generarLayoutCargaEntrega(String fecha, boolean guardar, int numeroSolicitud) {
		try {

			logBuilder = new StringBuilder();
			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;

			pintarEnlog(logBuilder, "####################--- Inicio layout carga de entrega ---####################",
					true);
			pintarEnlog(logBuilder, "1. Obteniendo solicitudes", true);

			if (numeroSolicitud == 0) {
				solicitudes = solUsPort.encontrarSolicitudesPorEventoYSistemaYEstatusNotIn(estatusCargaEntrega, "SAPB1",
						"3");
				// solicitudes =
				// solUsPort.encontrarSolicitudesPorEventoYEstatus(estatusCargaEntrega, "3");
			} else {
				solicitudes = new ArrayList<>();
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			pintarEnlog(logBuilder, "Núm. Solicitudes obtendidas " + solicitudes.size(), true);

			if (!guardar) {
				StringBuilder sols = new StringBuilder();
				for (SolicitudViaticosEntity s : solicitudes) {
					sols.append(s.getId() + " ");
				}
				pintarEnlog(logBuilder, "Id Solicitudes obtendidas: " + sols.toString(), true);
			}

			// Validar si obtuvo solicitudes
			if (!solicitudes.isEmpty()) {

				// Validacion si no se encontro periodo no agregar registro

				pintarEnlog(logBuilder, "2. Se comenzará hacer barrido de solicitudes para formar layout", true);
				// Llamar metodo que consulta periodo
				periodo = periodo(fecha);
				if (periodo != 0) {

					// Generar referencia
					referencia = numCE + periodo;
					pintarEnlog(logBuilder, "Referencia:" + referencia, true);
					// Crear builder para formar estructura
					StringBuilder builder = new StringBuilder();
					// Llamar metodo para agregar datos a layout
					agregarDatosALayoutCarga(builder, solicitudes);
					if (guardar) {
						// Llamar metodo para guardar archivo y bitacora
						guardarArchivoYBitacora(builder, estatusCargaEntrega, solicitudes, 8, archivoCeNombre, false,
								"En periodo " + periodo);
					}

				} else {
					sinPeriodo = true;
				}

				if (sinPeriodo) {
					pintarEnlog(logBuilder, "No se encontro periodo", true);
					guardarLog(8, logBuilder);
				}

				pintarEnlog(logBuilder, "####################--- Fin layout carga de entrega ---####################",
						true);

			} else {

				pintarEnlog(logBuilder, "2. No se obtuvieron solicitudes", true);
				pintarEnlog(logBuilder, "####################--- Fin layout carga de entrega ---####################",
						true);
				guardarLog(8, logBuilder);

			}

			// Barrido para solicitudes a SAPB1
			pintarEnlog(logBuilder, "####################--- Inicio dispersion a SAP B1 ---####################", true);
			pintarEnlog(logBuilder, "1. Revisando cuales solicitudes son de SAP B1", true);

			List<SolicitudViaticosEntity> solicitudesSABB1 = null;

			if (numeroSolicitud == 0) {
				solicitudesSABB1 = solUsPort.encontrarSolicitudesPorEventoYSistemaYEstatus(estatusCargaEntrega, "SAPB1",
						"3");
			} else {
				solicitudesSABB1 = new ArrayList<>();
				solicitudesSABB1.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			if (!solicitudesSABB1.isEmpty()) {

				pintarEnlog(logBuilder, "2. Se comenzará hacer barrido de solicitudes para enviar dispersion SAPB1",
						true);

				jobsSABB1.envioDispercionASAPB1(solicitudesSABB1, guardar, logBuilder);

			} else {
				pintarEnlog(logBuilder, "No se encontraron solicitudes a SAP B1", true);
			}

			pintarEnlog(logBuilder, "####################--- Fin dispersion a SAB B1 ---####################", true);

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaEntrega,
					"SE EJECUTO JOB CARGA ENTREGA SAPB1: " + solicitudes.size(), us);

		} catch (Exception e) {

			logBuilder.append("\n").append(UtilidadesAdapter.obtenerFechaYHoraActual() + " Error " + e.getMessage());
			guardarLog(8, logBuilder);

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaEntrega, "SE EJECUTO JOB CARGA PERO SE TUVO ERROR", us);

		}

		return logBuilder.toString();

	}

	@Override
	public String generarLayoutCargaComprobacion(String fecha, boolean guardar, int numeroSolicitud) {
		// Crear builder para formar estructura
		logBuilder = new StringBuilder();
		String datos = "";
		try {

			log.info("####################--- Inicio layout carga de comprobación "
					+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################");
			logBuilder.append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Iniciando con el layout de carga de comprobación ");
			log.info("1. Obteniendo solicitudes");
			logBuilder.append("\n")
					.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " 1. Obteniendo solicitudes");

			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;

			if (numeroSolicitud == 0) {
				solicitudes = solUsPort.encontrarSolicitudesPorEventoYEstatus(estatusCargaComprobacion, "14");
			} else {
				solicitudes = new ArrayList<>();
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			log.info(" Nuúm Solicitudes obtenidas:" + solicitudes.size());
			logBuilder.append("\n").append(
					UtilidadesAdapter.formatearFechaConHora(new Date()) + " Núm. obtendidas " + solicitudes.size());
			if (!guardar) {
				StringBuilder sols = new StringBuilder();
				for (SolicitudViaticosEntity s : solicitudes) {
					sols.append(s.getId() + " ");
				}
				log.info(" Id Solicitudes obtenidas:" + sols.toString());
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " Id Solicitudes obtendidas: " + sols.toString());
			}

			// Validar si obtuvo solicitudes
			if (!solicitudes.isEmpty()) {

				log.info("2. Se comenzará hacer barrido de solicitudes para formar layout");
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " 2. Se comenzará hacer barrido de solicitudes para formar layout");

				// Llamar metodo que consulta periodo
				periodo = periodo(fecha);
				if (periodo != 0) {
					// Generar referencia
					referencia = numCC + periodo;
					log.info("Referencia:" + referencia);
					logBuilder.append("\n")
							.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " Referencia:" + referencia);
					// Crear builder para formar estructura
					StringBuilder builder = new StringBuilder();
					// Llamar metodo para agregar datos a layout
					datos = agregarDatosALayoutComprobacionN(builder, solicitudes);
					if (guardar) {
						// Llamar metodo para guardar archivo y bitacora
						guardarArchivoYBitacora(builder, estatusCargaComprobacion, solicitudes, 9, archivocCcNombre,
								false, "En periodo " + periodo);
					}

				} else {
					sinPeriodo = true;
				}

				if (sinPeriodo) {
					log.info(UtilidadesAdapter.formatearFechaConHora(new Date()) + " No se encontro periodo");
					logBuilder.append("\n")
							.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " No se encontro periodo");
					logBuilder.append("\n").append(
							UtilidadesAdapter.formatearFechaConHora(new Date()) + " 2. No se obtuvieron solicitudes");
					log.info("####################--- Fin layout carga de entrega "
							+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################" + "\n");
					guardarLog(9, logBuilder);
				}

			} else {

				log.info("2. No se obtuvieron solicitudes");
				logBuilder.append("\n").append(
						UtilidadesAdapter.formatearFechaConHora(new Date()) + " 2. No se obtuvieron solicitudes");
				log.info("####################--- Fin layout carga de comprobación "
						+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################" + "\n");
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " Finalinzado el layout de carga de comprobación");
				guardarLog(9, logBuilder);

			}

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaComprobacion,
					"SE EJECUTO JOB CARGA COMPROBACION: " + solicitudes.size(), us);

		} catch (Exception e) {
			e.printStackTrace();
			logBuilder.append("\n").append(UtilidadesAdapter.obtenerFechaYHoraActual() + " Error " + e.getMessage());
			guardarLog(9, logBuilder);

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaEntrega, "SE EJECUTO JOB CARGA PERO SE TUVO ERROR", us);

		}
		if ("".equals(datos)) {
			return logBuilder.toString();
		} else {
			return datos;
		}
	}

	private void agregarDatosALayoutCarga(StringBuilder builder, List<SolicitudViaticosEntity> solicitudes) {

		List<layoutComprobacion> listaLayout = new ArrayList<>();
		layoutComprobacion layout = null;
		StringBuilder l = new StringBuilder();

		StringBuilder sbSol = new StringBuilder();
		for (SolicitudViaticosEntity solicitud : solicitudes) {

			boolean validaSiTieneYaEvento = eventoDeViaticosPort.tieneEventoLaSolicitud(solicitud.getId(),
					estatusCargaEntrega);

			if (!validaSiTieneYaEvento) {
				if (periodo != 0) {
					layout = new layoutComprobacion();
					layout.setUsuario(solicitud.getUsuario());
					layout.setTipo(concepto.D003_700());
					layout.setMonto(solicitud.getAnticipo().doubleValue());
					layout.setReferencia(String.valueOf(referencia));
					listaLayout.add(layout);
					l.append(layout.toString() + "\n");
					sbSol.append(solicitud.getId() + ",");

				} else {
					logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
							+ " [ Error ]: No se encontro periodo en la fecha " + new Date());
				}
			} else {
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " [ Error ]: La solicitud " + solicitud.getId() + " ya estaba en layout de carga");
			}

		}

		if (sbSol.length() != 0) {
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Se cargaron al layout las solicitudes: [" + sbSol.substring(0, sbSol.length() - 1) + "]");
		}

		log.info("\n" + l.toString());
		log.info("Agrupado antes:" + listaLayout.size());
		String s = obtenerAgrupado(listaLayout, referencia, 1);
		log.info("Se obtuvo agrupado" + s.length());
		log.info("Agrupado:\n" + s);
		builder.append(s);

		log.info("3. Se termina de formar los datos de estrucura de archivo");
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
				+ " 3. Se termina de formar los datos de estrucura de archivo");

		logBuilder.append("\n").append(l.toString() + "\nLayout sin agrupar por usuario:\n---" + l.toString()
				+ "\n\nLayout agrupado por usuario:\n---\n" + s);
	}

	private String agregarDatosALayoutComprobacionN(StringBuilder builder, List<SolicitudViaticosEntity> solicitudes) {

		String usuario = "";
		String usuarioAnterior = "";
		int count = 1;
		int tamanio = solicitudes.size();

		BigDecimal totalNoDeducibleS = BigDecimal.ZERO;
		BigDecimal totalDeducibleS = BigDecimal.ZERO;
		BigDecimal totalNoAplicaS = BigDecimal.ZERO;
		BigDecimal totalReintegroS = BigDecimal.ZERO;
		BigDecimal diferenciaS = BigDecimal.ZERO;
		BigDecimal gravadoS = BigDecimal.ZERO;
		BigDecimal anticipoSolicitudS = BigDecimal.ZERO;
		BigDecimal totalNoDeducibleAnualS = BigDecimal.ZERO;

		Map<Integer, BigDecimal> mapSalida = new HashMap<>();
		Map<Integer, BigDecimal> mapSumas = new HashMap<>();

		List<layoutComprobacion> listaLayout = new ArrayList<>();

		StringBuilder l = new StringBuilder();
		StringBuilder l2 = new StringBuilder();

		String ejercicio = "";
		if (!solicitudes.isEmpty()) {
			Date d = new Date();
			String fechaCadena = UtilidadesAdapter.formatearFecha(d);
			ejercicio = fechaCadena.substring(0, 4);
		}

		StringBuilder sbSol = new StringBuilder();
		for (SolicitudViaticosEntity solicitud : solicitudes) {

			System.out.println("Solicitud:" + solicitud.getId());

			boolean validaSiTieneYaEvento = eventoDeViaticosPort.tieneEventoLaSolicitud(solicitud.getId(),
					estatusCargaComprobacion);

			if (!validaSiTieneYaEvento) {

				if (periodo != 0) {

					usuario = solicitud.getUsuario();
					if (count == 1) {
						usuarioAnterior = usuario;
					}

					BigDecimal totalPropina = cfdiPort.totalPropina(solicitud.getId());

					BigDecimal totalComprobado = cfdiPort.totalComprobado(solicitud.getId());
					BigDecimal totalNoDeducible = cfdiPort.totalComprobadoNoDeducible(solicitud.getId());
					totalNoDeducible = totalNoDeducible.add(totalPropina);
					BigDecimal totalDeducible = cfdiPort.totalComprobadoDeducible(solicitud.getId());
					//BigDecimal totalNoAplica = cfdiPort.totalNoAplica(solicitud.getId(), true);
					//Ajuste de monto aprobado a pagar
					BigDecimal totalNoAplica = calculoNoAplica(solicitud.getComprobanteViaticosEntity());
					BigDecimal totalComprobadoValido = totalComprobado.subtract(totalNoAplica);
					BigDecimal totalNoDeducibleAnual = cfdiPort.totalNoDeduciblePorAnio(ejercicio,
							solicitud.getUsuario());
					BigDecimal totalReintegro = cfdiPort.totalReintegroPorSolicitud(solicitud.getId());

					String porcentajeNoDeduciblePorSolicitudString = confPort.obtenerConfiguracion(5).getValor1();
					BigDecimal totalPorcentajeNoDeduciblePorSolicitud = porcentajeNoDeduciblePorSolicitudString == null
							? new BigDecimal("0.00")
							: new BigDecimal(porcentajeNoDeduciblePorSolicitudString);
					String porcentajeNoDeducibleAnualString = confPort.obtenerConfiguracion(4).getValor1();
					BigDecimal topeTotalNoDeducibleAnual = porcentajeNoDeducibleAnualString == null
							? new BigDecimal("0.00")
							: new BigDecimal(porcentajeNoDeducibleAnualString);
					BigDecimal Propina = cfdiPort.totalPropina(solicitud.getId());
					
					
							
					Map<Integer, BigDecimal> map = new HashMap<>();
					map.put(1, totalComprobado);
					map.put(2, totalNoDeducible);
					map.put(3, totalDeducible);
					map.put(4, totalNoAplica);
					map.put(5, totalComprobadoValido);

					l.append("\n total Anula sumado " + totalNoDeducibleAnualS);
					l.append("\n total Anula normal " + totalNoDeducibleAnual);
					if (totalNoDeducibleAnualS.compareTo(totalNoDeducibleAnual) == 1) {
						l.append("\n Tomo total no deducible anual sumado");
						System.out.println("Tomo total anual sumado");
						map.put(6, totalNoDeducibleAnualS);
					} else {
						l.append("\n Tomo total no deducible anual normal");
						System.out.println("Tomo total anual normal");
						map.put(6, totalNoDeducibleAnual);
					}
					map.put(7, totalReintegro);
					map.put(8, totalPorcentajeNoDeduciblePorSolicitud);
					map.put(9, topeTotalNoDeducibleAnual);
					map.put(10, Propina);

					if (usuarioAnterior.equals(usuario)) {
						// l.append("\n count:"+count);
						// l.append(" size:"+tamanio);
						if (count == 1) {
							l.append("\n \n Comienza calculo de solicitudes de usuario " + usuario);
						} else {
							l.append("\n \n Continua calculo de solicitudes de usuario " + usuario);
						}

					} else {
						l.append("\n \n<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>");
						l.append("\n Se formara layout del usuario " + usuarioAnterior);
						generarLayoutComprobacion(l, l2, listaLayout, mapSumas, usuarioAnterior);
						l.append("\n <<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>");

						l.append("\n \n Usuario cambio " + usuario);

						totalNoDeducibleS = BigDecimal.ZERO;
						totalDeducibleS = BigDecimal.ZERO;
						totalNoAplicaS = BigDecimal.ZERO;
						totalReintegroS = BigDecimal.ZERO;
						diferenciaS = BigDecimal.ZERO;
						gravadoS = BigDecimal.ZERO;
						anticipoSolicitudS = BigDecimal.ZERO;
						totalNoDeducibleAnualS = BigDecimal.ZERO;
						map.put(6, totalNoDeducibleAnual);
					}
					usuarioAnterior = usuario;
					count++;

					l.append(
							"\n __________________________________________________________________________________________________________");
					l.append("\n  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! [ Solicitud " + solicitud.getId()
							+ " ] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

					mapSalida = calculoMontosPorSolicitud(l, map, solicitud);

					System.out.println("map:" + mapSalida.get(1));

					totalNoDeducibleS = totalNoDeducibleS.add(mapSalida.get(1));
					totalDeducibleS = totalDeducibleS.add(mapSalida.get(2));
					totalNoAplicaS = totalNoAplicaS.add(mapSalida.get(3));
					totalReintegroS = totalReintegroS.add(mapSalida.get(4));
					diferenciaS = diferenciaS.add(mapSalida.get(5));
					gravadoS = gravadoS.add(mapSalida.get(6));
					anticipoSolicitudS = anticipoSolicitudS.add(mapSalida.get(7));
					// totalNoDeducibleAnualS = totalNoDeducibleAnualS.add(mapSalida.get(8));
					totalNoDeducibleAnualS = mapSalida.get(8);

					l.append("\n Suma deducible:                                          |" + totalDeducibleS
							+ "\n Suma no deducible [Sin xml + Propina]:                   |" + totalNoDeducibleS
							+ "\n Suma no aplica:                                          |" + totalNoAplicaS
							+ "\n Suma reingreso:                                          |" + totalReintegroS
							+ "\n Suma diferencia:                                         |" + diferenciaS
							+ "\n Suma gravado:                                            |" + gravadoS
							+ "\n Suma anticipoSolicitud:                                  |" + anticipoSolicitudS
							+ "\n Suma total NO deducible anual                            |" + totalNoDeducibleAnualS);

					mapSumas.put(1, totalDeducibleS);
					mapSumas.put(2, totalNoDeducibleS);
					mapSumas.put(3, totalNoAplicaS);
					mapSumas.put(4, totalReintegroS);
					mapSumas.put(5, diferenciaS);
					mapSumas.put(6, gravadoS);
					mapSumas.put(7, anticipoSolicitudS);

					// calculoComprobacionLayout(l, l2, listaLayout, map, solicitud);
					sbSol.append(solicitud.getId());

					if (tamanio == count - 1) {
						l.append("\n \n<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>");
						l.append("\n Se formara layout del usuario " + usuarioAnterior);
						generarLayoutComprobacion(l, l2, listaLayout, mapSumas, usuarioAnterior);
						l.append("\n <<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>");
					}

				} else {
					logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
							+ " [ Error ]: No se encontro periodo en la fecha " + new Date());
				}
			} else {
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
						+ " [ Error ]: La solicitud " + solicitud.getId() + " ya estaba en layout de comprobacion");
			}

		}

		if (sbSol.length() != 0) {
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
					+ " Se cargaron al layout las solicitudes: [" + sbSol.substring(0, sbSol.length() - 1) + "]");
		}

		log.info("\n....." + l.toString() + "\n.....");
		log.info(
				"\n==================================================================================================================================");

		Collections.sort(listaLayout);
		log.info("\n....." + l2.toString() + "\n.....");
		String s = obtenerAgrupado(listaLayout, referencia, 2);
		log.info("Layout sin agrupar usuario:\n" + s);
		builder.append(s);

		log.info("3. Se termina de formar los datos de estrucura de archivo");
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date())
				+ " 3. Se termina de formar los datos de estrucura de archivo");

		return l.toString() + "\n \n LAYOUT SIN AGRUPAR POR USUARIO\n-----------------------------" + l2.toString()
				+ "\n\n LAYOUT AGRUPADO POR USUARIO\n------------------------------\n" + s;
	}

	public Map<Integer, BigDecimal> calculoMontosPorSolicitud(StringBuilder l, Map<Integer, BigDecimal> map,
			SolicitudViaticosEntity solicitud) {

		BigDecimal totalComprobado = map.get(1);
		BigDecimal totalNoDeducible = map.get(2);
		BigDecimal totalDeducible = map.get(3);
		BigDecimal totalNoAplica = map.get(4);
		// BigDecimal totalComprobadoValido = map.get(5);
		BigDecimal totalNoDeducibleAnual = map.get(6);
		BigDecimal totalReintegro = map.get(7);
		BigDecimal porcentajeNoDeduciblePorSolicitud = map.get(8);
		BigDecimal topeTotalNoDeducibleAnual = map.get(9);
		BigDecimal propina = map.get(10);
		BigDecimal anticipoSolicitud = solicitud.getAnticipo();

		BigDecimal totalPorcentajeNoDeducible = BigDecimal.ZERO;
		BigDecimal gravado = BigDecimal.ZERO;

		BigDecimal diferencia = BigDecimal.ZERO;

//		totalPorcentajeNoDeducible = porcentajeNoDeduciblePorSolicitud.multiply(totalComprobadoValido)
//				.divide(new BigDecimal("100"));

		System.out.println("Solicitud:" + solicitud.getId());
		l.append(
				"\n __________________________________________________________________________________________________________");
		l.append("\n Usuario:                                                 |" + solicitud.getUsuario() + "-"
				+ solicitud.getNombreCompletoUsuario() + "\n Anticipo:                                                |"
				+ anticipoSolicitud + "\n Total comprobado [Comprobado + Propina]:                 |" + totalComprobado
				+ "\n Propina:                                                 |" + propina
				+ "\n Tope no deducible anual:                                 |" + topeTotalNoDeducibleAnual
				+ "\n Porcentaje no deducible por sol.:                        |" + porcentajeNoDeduciblePorSolicitud
				+ "\n Total porcentaje no deducible.:                          |" + totalPorcentajeNoDeducible
				+ "\n Total no deducible anual.:                               |" + totalNoDeducibleAnual
				+ "\n __________________________________________________________________________________________________________"
				+ "\n Total deducible:                                         |" + totalDeducible
				+ "\n Total no deducible [Sin xml + Propina]:                  |" + totalNoDeducible
				+ "\n Total no aplica:                                         |" + totalNoAplica
				+ "\n Total reingreso:                                         |" + totalReintegro);

		// Calcular diferencia
		diferencia = diferencia.add(anticipoSolicitud);
		diferencia = diferencia.subtract(totalDeducible);
		diferencia = diferencia.subtract(totalNoDeducible);
		diferencia = diferencia.subtract(totalReintegro);
		diferencia = diferencia.add(totalNoAplica);

		l.append("\n Diferencia    :                                          |" + diferencia);

		BigDecimal sumaMontoNoDeducibleAnual = BigDecimal.ZERO;
		BigDecimal sumaMontoNoDeducibleAnualSinAlterar = BigDecimal.ZERO;
		BigDecimal importe702 = BigDecimal.ZERO;
		BigDecimal calculoNoDeducible = BigDecimal.ZERO;

		sumaMontoNoDeducibleAnualSinAlterar = totalNoDeducibleAnual.add(totalNoDeducible);

		importe702 = importe702.add(totalDeducible);
		importe702 = importe702.add(totalNoDeducible);
		importe702 = importe702.subtract(totalNoAplica);

		totalPorcentajeNoDeducible = porcentajeNoDeduciblePorSolicitud.multiply(importe702)
				.divide(new BigDecimal("100"));

		if (totalNoDeducible.compareTo(totalPorcentajeNoDeducible) == 1) {
			// El no de ducible se queda igual
			calculoNoDeducible = totalPorcentajeNoDeducible;
		} else {

			calculoNoDeducible = totalNoDeducible;
		}

		// Calcular gravado
		sumaMontoNoDeducibleAnual = totalNoDeducibleAnual.add(calculoNoDeducible);
		if (sumaMontoNoDeducibleAnual.compareTo(topeTotalNoDeducibleAnual) >= 0) {
			l.append("\n Supero el tope de no deducible anual");
			gravado = sumaMontoNoDeducibleAnual.subtract(topeTotalNoDeducibleAnual);
		} else {
			System.out.println(totalNoDeducible.compareTo(calculoNoDeducible) == 1);
			if (totalNoDeducible.compareTo(calculoNoDeducible) == 1) {
				l.append("\n Alcanza a tomar lo del porcentaje no deducible");
				gravado = totalNoDeducible.subtract(totalPorcentajeNoDeducible);
			} else {
				// Se deja gravado en 0
			}
		}

		l.append("\n Gravado       :                                          |" + gravado + "\n _________________"
				+ "\n Total no deducible anual sumado:                         |" + sumaMontoNoDeducibleAnualSinAlterar
				+ "\n __________________________________________________________________________________________________________");

		Map<Integer, BigDecimal> mapSalida = new HashMap<>();
		mapSalida.put(1, totalNoDeducible);
		mapSalida.put(2, totalDeducible);
		mapSalida.put(3, totalNoAplica);
		mapSalida.put(4, totalReintegro);
		mapSalida.put(5, diferencia);
		mapSalida.put(6, gravado);
		mapSalida.put(7, anticipoSolicitud);
		mapSalida.put(8, sumaMontoNoDeducibleAnualSinAlterar);

		return mapSalida;
	}

	public void generarLayoutComprobacion(StringBuilder l, StringBuilder l2, List<layoutComprobacion> listaLayout,
			Map<Integer, BigDecimal> map, String usuario) {

		BigDecimal totalNoDeducibleS = BigDecimal.ZERO;
		BigDecimal totalDeducibleS = BigDecimal.ZERO;
		BigDecimal totalNoAplicaS = BigDecimal.ZERO;
		BigDecimal totalReintegroS = BigDecimal.ZERO;
		BigDecimal diferenciaS = BigDecimal.ZERO;
		BigDecimal gravadoS = BigDecimal.ZERO;
		BigDecimal anticipoSolicitudS = BigDecimal.ZERO;

		totalDeducibleS = totalDeducibleS.add(map.get(1));
		totalNoDeducibleS = totalNoDeducibleS.add(map.get(2));
		totalNoAplicaS = totalNoAplicaS.add(map.get(3));
		totalReintegroS = totalReintegroS.add(map.get(4));
		diferenciaS = diferenciaS.add(map.get(5));
		gravadoS = gravadoS.add(map.get(6));
		anticipoSolicitudS = anticipoSolicitudS.add(map.get(7));

		layoutComprobacion layout = null;
		l.append("\nDiferencia final:" + diferenciaS + "\n");
		l.append("\n[LAYOUT]");

		// Calcular diferencia
		diferenciaS = BigDecimal.ZERO;
		diferenciaS = diferenciaS.add(anticipoSolicitudS);
		diferenciaS = diferenciaS.subtract(totalDeducibleS);
		diferenciaS = diferenciaS.subtract(totalNoDeducibleS);
		diferenciaS = diferenciaS.subtract(totalReintegroS);
		diferenciaS = diferenciaS.add(totalNoAplicaS);

		if (diferenciaS.signum() == -1) {

			// 700
			diferenciaS = diferenciaS.multiply(new BigDecimal("-1.00"));
			layout = new layoutComprobacion();
			layout.setUsuario(usuario);
			layout.setMonto(diferenciaS.doubleValue());
			layout.setTipo(concepto.D003_700());
			listaLayout.add(layout);
			l2.append("\n" + layout.toString());
			l.append("\n" + layout.toString());
			diferenciaS = BigDecimal.ZERO;

		} else {

			// 703
			layout = new layoutComprobacion();
			layout.setUsuario(usuario);
			layout.setMonto(diferenciaS.doubleValue());
			layout.setTipo(concepto.D080_703());
			listaLayout.add(layout);
			l2.append("\n" + layout.toString());
			l.append("\n" + layout.toString());

			// 706
			layout = new layoutComprobacion();
			layout.setUsuario(usuario);
			layout.setMonto(diferenciaS.doubleValue());
			layout.setTipo(concepto.D004_706());
			listaLayout.add(layout);
			l2.append("\n" + layout.toString());
			l.append("\n" + layout.toString());

		}

		// 704
		layout = new layoutComprobacion();
		layout.setUsuario(usuario);
		layout.setMonto(totalReintegroS.doubleValue());
		layout.setTipo(concepto.D004_704());
		listaLayout.add(layout);
		l2.append("\n" + layout.toString());
		l.append("\n" + layout.toString());

		totalDeducibleS = totalDeducibleS.add(totalNoDeducibleS);
		totalDeducibleS = totalDeducibleS.subtract(totalNoAplicaS);
		// 702
		layout = new layoutComprobacion();
		layout.setUsuario(usuario);
		layout.setMonto(totalDeducibleS.doubleValue());
		layout.setTipo(concepto.D081_702());
		listaLayout.add(layout);
		l2.append("\n" + layout.toString());
		l.append("\n" + layout.toString());

		totalDeducibleS = totalDeducibleS.add(totalReintegroS);
		totalDeducibleS = totalDeducibleS.add(diferenciaS);
		// 701
		layout = new layoutComprobacion();
		layout.setUsuario(usuario);
		layout.setMonto(totalDeducibleS.doubleValue());
		layout.setTipo(concepto.D050_701());
		listaLayout.add(layout);
		l2.append("\n" + layout.toString());
		l.append("\n" + layout.toString());

		// 705
		layout = new layoutComprobacion();
		layout.setUsuario(usuario);
		layout.setMonto(gravadoS.doubleValue());
		layout.setTipo(concepto.D050_705());
		listaLayout.add(layout);
		l2.append("\n" + layout.toString());
		l.append("\n" + layout.toString());

	}

	private BigDecimal calculoNoAplica(List<ComprobanteViaticoEntity> listaComprobantes) {
		BigDecimal na = BigDecimal.ZERO;
		BigDecimal monto = BigDecimal.ZERO;
		BigDecimal montoAprobado = BigDecimal.ZERO;
		
		for (ComprobanteViaticoEntity co : listaComprobantes) {
			log.info("Comprobante:" + co.getId());
			if (co.isAprobacionNoAplica()) {
				if (co.getMontoAprobado() != null) {
					montoAprobado = co.getMontoAprobado();
				}else {
					montoAprobado = BigDecimal.ZERO;
				}
				log.info("Monto aprobado:" + montoAprobado);
				if (montoAprobado.compareTo(new BigDecimal("0.0")) == 0) {
					na = na.add(BigDecimal.ZERO);
				} else {
					monto = co.getTotal().subtract(montoAprobado);
					log.info("Monto total - monto aprobado = no aplica:" + monto);
					na = na.add(monto);
				}
			} else {
				na = na.add(co.getNoAplica());
				log.info("Monto no aplica directo:"+na);
			}
		}
		log.info("Calculo de monto no aplica total:"+na);
		return na;
	}

	private void guardarArchivoYBitacora(StringBuilder builder, String evento,
			List<SolicitudViaticosEntity> solicitudes, int idConfig, String rutaArchivo, boolean cambiarEstatus,
			String textoEvento) {

		int numero = 4;

		// Si el builder es diferente vacio si no entonces no es para layout
		if (builder != null) {
			if ("qas".equals(ambiente)) {
				log.info(numero + ". Se guardará el archivo en ruta "
						+ confPort.obtenerConfiguracion(idConfig).getValor1() + "qas/");
				logBuilder.append("\n")
						.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + numero
								+ " . Se guardará el archivo en ruta "
								+ confPort.obtenerConfiguracion(idConfig).getValor1() + "qas/");
				// Obtener ruta de las configuraciones
				archivosPort.guardarArchivoDeLayout(builder.toString(),
						confPort.obtenerConfiguracion(idConfig).getValor1() + "qas/", rutaArchivo);
			} else if ("pro".equals(ambiente)) {
				log.info(numero + ". Se guardará el archivo en ruta "
						+ confPort.obtenerConfiguracion(idConfig).getValor1());
				logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + numero
						+ " . Se guardará el archivo en ruta " + confPort.obtenerConfiguracion(idConfig).getValor1());
				// Obtener ruta de las configuraciones
				archivosPort.guardarArchivoDeLayout(builder.toString(),
						confPort.obtenerConfiguracion(idConfig).getValor1(), rutaArchivo);
			}
			numero++;
		} else {

		}
		for (SolicitudViaticosEntity solicitud : solicitudes) {
			// Agregar evento de generacion de layout
			agregarEvento(evento, "", solicitud);
		}
		if (cambiarEstatus) {
			log.info(numero + ". Se agrego eventos a las solicitudes y se cambiaron estatus");
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + numero
					+ " . Se agrego eventos a las solicitudes y se cambiaron estatus");
			numero++;
		} else {
			log.info(numero + ". Se agrego eventos a las solicitudes");
			logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + numero
					+ " . Se agrego eventos a las solicitudes");
			numero++;
		}
		log.info(numero + ". Se guardo el archivo de layout exitosamente");
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + numero
				+ " . Se guardo el archivo de layout exitosamente");
		numero++;

		log.info("####################--- Fin proceso " + UtilidadesAdapter.formatearFechaConHora(new Date())
				+ " ---####################" + "\n");
		logBuilder.append("\n").append(
				UtilidadesAdapter.formatearFechaConHora(new Date()) + " Finalinzado el layout de carga de entrega");
		log.info("Guardando archivo log");
		guardarLog(idConfig, logBuilder);
	}

	@Override
	public void guardarLog(int tipo, StringBuilder sb) {
		String fechaLog = UtilidadesAdapter.obtenerFechaYHoraActual().replace(":", "").replace("-", "").replace(" ", "")
				.replace(" ", "");
		fechaLog = fechaLog.substring(0, 4) + "_" + fechaLog.substring(4, 6) + "_" + fechaLog.substring(6, 8);

		Date d = new Date();
		String fechaCadena = UtilidadesAdapter.formatearFecha(d);
		String ejercicio = fechaCadena.substring(0, 4);

		String rutaLog = "";
		if (guardarLog) {
			if (tipo == 1) {
				rutaLog = rutaArchivoLogGeneral;
			} else if (tipo == 8) {
				rutaLog = rutaArchivoLogEntrega;
			} else if (tipo == 9) {
				rutaLog = rutaArchivoLogComprobacion;
			} else if (tipo == 10) {
				rutaLog = rutaArchivoLogFox;
			} else if (tipo == 11) {
				rutaLog = rutaArchivoLogSys21;
			} else if (tipo == 12) {
				rutaLog = rutaArchivoLogSABB1;
			}
			if ("qas".equals(ambiente)) {
				archivosPort.guardarArchivoDeLayout(sb.toString(), rutaLog + ejercicio + "/qas",
						"/log_" + fechaLog + ".txt");
			} else if ("pro".equals(ambiente)) {
				archivosPort.guardarArchivoDeLayout(sb.toString(), rutaLog + ejercicio, "/log_" + fechaLog + ".txt");
			}
		}
	}

	private int periodo(String fecha) throws Exception {

		String fechaCadena = "";
		String ejercicio = "";
		Date d = null;

		// Se toma el periodo y con el ejercicio actual
		d = new Date();
		fechaCadena = UtilidadesAdapter.formatearFecha(d);
		ejercicio = fechaCadena.substring(0, 4);

		log.info("Tomando periodo de BD Nu3");

		if(ambiente.equals("test")) {
			periodo = 504;
		}else {
		if (!fecha.equals("")) {
			fechaCadena = fecha;
			ejercicio = fecha.substring(0, 4);
			periodo = periodoPort.obtenerPeriodo(fechaCadena, ejercicio);
		} else {

			periodo = periodoPort.obtenerPeriodo(fechaCadena, ejercicio);
		}
		}
		logBuilder.append("\n")
				.append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " Tomando periodo de BD Nu3");
		log.info("Fecha: " + fechaCadena + " / ejercicio: " + ejercicio + " de Nu3");
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " Fecha: " + fechaCadena
				+ " / ejercicio: " + ejercicio + " de Nu3");
		return periodo;
	}

	@Override
	public void agregarEvento(String evento, String texto, SolicitudViaticosEntity solicitud) {
		solicitud.setNombreCompletoUsuario("JOB");
		eventoDeViaticosPort.ingresarEventoDeSolicitud(evento, texto, solicitud,
				solicitud.getNombreCompletoUsuario().toUpperCase());
	}

	@Override
	public String obtenerAgrupado(List<layoutComprobacion> list, int referencia, int tipo) {
		// Tipo 1 es carga entrega tipo 2 es carga comprobacion

		List<layoutComprobacion> listSalida = new ArrayList<>();
		layoutComprobacion layout = null;
		int refSinValor = 0;
		int refInicial = 0;
		StringBuilder salida = new StringBuilder();
		Map<String, Map<String, DoubleSummaryStatistics>> g = list.stream()
				.collect(Collectors.groupingBy(layoutComprobacion::getTipo, groupingBy(layoutComprobacion::getUsuario,
						Collectors.summarizingDouble(layoutComprobacion::getMonto))));

		for (Entry<String, Map<String, DoubleSummaryStatistics>> entry : g.entrySet()) {
			// log.info("g.entrySet:" + entry.getKey());
			Map<String, DoubleSummaryStatistics> li = entry.getValue();
			for (Entry<String, DoubleSummaryStatistics> e : li.entrySet()) {
				if (tipo == 1) {
					layout = new layoutComprobacion();
					layout.setUsuario(e.getKey());
					layout.setMonto(e.getValue().getSum());
					layout.setTipo(entry.getKey());
					layout.setReferencia(String.valueOf(referencia));
					listSalida.add(layout);
				} else {
					refSinValor = referencia - numCC;
					refInicial = referencia - refSinValor;
					// log.info("refSinValor:"+refSinValor);
					// log.info("refInicial:"+refInicial);
					if (refInicial == numCC) {
						if (entry.getKey() == concepto.D004_706()) {
							layout = new layoutComprobacion();
							layout.setUsuario(e.getKey());
							layout.setMonto(e.getValue().getSum());
							layout.setTipo(entry.getKey());
							layout.setReferencia(String.valueOf(referencia));
							listSalida.add(layout);
						} else {
							layout = new layoutComprobacion();
							layout.setUsuario(e.getKey());
							layout.setMonto(e.getValue().getSum());
							layout.setTipo(entry.getKey());
							layout.setReferencia(String.valueOf(referencia));
							listSalida.add(layout);
						}
					} else {
						layout = new layoutComprobacion();
						layout.setUsuario(e.getKey());
						layout.setMonto(e.getValue().getSum());
						layout.setTipo(entry.getKey());
						layout.setReferencia(String.valueOf(referencia));
						listSalida.add(layout);
					}
				}
			}
		}
		Collections.sort(listSalida);
		for (layoutComprobacion l : listSalida) {
			salida.append(l.getUsuario() + "," + l.getTipo() + "," + l.getMonto() + "," + l.getReferencia() + "\n");
		}
		return salida.toString();
	}

	@Override
	public String limpiarSolicitudesFueraRango(int numeroSolicitud, boolean guardar) {
		try {

			logBuilder = new StringBuilder();

			log.info("####################--- Inicio limpieza de Solicitudes fuera de rango "
					+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################");
			logBuilder.append(UtilidadesAdapter.textoLog("Inicio limpieza de Solicitudes fuera de rango "));
			log.info("1. Obteniendo solicitudes");
			logBuilder.append(UtilidadesAdapter.textoLog("1. Obteniendo solicitudes"));

			// Buscar solicitudes que no se hallan enviado a carga
			List<SolicitudViaticosEntity> solicitudes = null;

			ConfiguracionEntity configuracionDias = new ConfiguracionEntity();
			configuracionDias = confPort.obtenerConfiguracion(1);
			logBuilder.append(UtilidadesAdapter.textoLog("Dias permitidos:" + configuracionDias.getValor1()));

			solicitudes = new ArrayList<>();
			int dias = Integer.valueOf(configuracionDias.getValor1());

			if (numeroSolicitud == 0) {
				solicitudes.addAll(solUsPort.obtenerSolicitudesFueraRango(dias));
			} else {
				solicitudes.add(solUsPort.obtenerSolicitudJPA(numeroSolicitud));
			}

			log.info(" Núm. Solicitudes obtenidas:" + solicitudes.size());
			logBuilder.append(UtilidadesAdapter.textoLog("Núm. Solicitudes obtendidas " + solicitudes.size()));

			if (!guardar) {
				StringBuilder sols = new StringBuilder();
				for (SolicitudViaticosEntity s : solicitudes) {
					sols.append(s.getId() + " ");
				}
				log.info(" Id Solicitudes obtenidas:" + sols.toString());
				logBuilder.append(UtilidadesAdapter.textoLog("Id Solicitudes obtendidas: " + sols.toString()));
			}

			// Validar si obtuvo solicitudes
			if (!solicitudes.isEmpty()) {

				for (SolicitudViaticosEntity solicitud : solicitudes) {

					if (guardar) {
						agregarEvento(eventoSolicitudFueraRango, "Sobrepaso el tiempo limite de carga y aprobación",
								solicitud);
						// Cambiando estatus a solicitud
						EstatusSolicitudEntity estatus = new EstatusSolicitudEntity();
						estatus = estatusPort.obtieneEstatusSolicitud(11);
						if (estatus == null)
							throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró estatus");
						solUsPort.enviaPeticionAceptacion(Integer.valueOf(solicitud.getId()), estatus);
					}

				}

			} else {

				log.info("2. No se obtuvieron solicitudes");
				logBuilder.append(UtilidadesAdapter.textoLog("2. No se obtuvieron solicitudes"));
				log.info("####################--- Finalizado limpieza de Solicitudes fuera de rango "
						+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################" + "\n");
				logBuilder.append(UtilidadesAdapter.textoLog("Finalizado limpieza de Solicitudes fuera de rango"));
				guardarLog(1, logBuilder);

			}

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SsOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaEntrega,
					"SE EJECUTO JOB CARGA LIMPIEZA SOLS: " + solicitudes.size(), us);

			log.info("####################--- Finalizado limpieza de Solicitudes fuera de rango "
					+ UtilidadesAdapter.formatearFechaConHora(new Date()) + " ---####################" + "\n");
			logBuilder.append(UtilidadesAdapter.textoLog("Finalizado limpieza de Solicitudes fuera de rango"));
			guardarLog(1, logBuilder);

		} catch (

		Exception e) {

			logBuilder.append(UtilidadesAdapter.textoLog("Error " + e.getMessage()));
			guardarLog(1, logBuilder);

			// Agregar a evento
			String us = "";
			if (guardar)
				us = "PORTAL";
			else
				us = "MANUAL SOLO INFO";
			eventoPort.insertarEventoCompleto(eventoCargaEntrega, "SE EJECUTO JOB LIMPIEZA SOLS. PERO SE TUVO ERROR",
					us);

		}

		return logBuilder.toString();
	}

	public static void main(String args[]) {
		
		BigDecimal na = BigDecimal.ZERO;
		BigDecimal montoAprobado = BigDecimal.ZERO;
		BigDecimal montoAprobadoI = BigDecimal.ZERO;
		montoAprobadoI = montoAprobadoI.add(new BigDecimal("0.00"));
		
		System.out.println("montoI:"+montoAprobadoI+"-montoA:"+montoAprobado);
		if (montoAprobadoI != null) {
			montoAprobado = montoAprobado.add(montoAprobadoI);
		}
		if (montoAprobado.compareTo(BigDecimal.ZERO) == 0) {
			na = na.add(BigDecimal.ZERO);
		} else {
			na = na.add(new BigDecimal("100"));
		}
		System.out.println("na:"+na);
	}

	public void pintarEnlog(StringBuilder logBuilder, String datos, boolean aConsola) {
		if (aConsola) {
			log.info(datos);
		}
		logBuilder.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " " + datos);
	}

}
