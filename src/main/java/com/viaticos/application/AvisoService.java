package com.viaticos.application;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.in.AvisoUseCase;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.ConfiguracionesPort;
import com.viaticos.application.port.out.NivelesPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.application.port.out.jpa.mysql.ComprobanteViaticoJPA;
import com.viaticos.domain.Aviso;
import com.viaticos.domain.ConfiguracionEntity;
import com.viaticos.domain.NivelTopeUsuarioEntity;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.Usuario;

@Service
public class AvisoService implements AvisoUseCase {
	
	BigDecimal DesayunoPorDia = null;
	BigDecimal ComidaPorDia = null;
	BigDecimal CenaPorDia = null;
	BigDecimal HospedajePorDia = null;
	BigDecimal PorcentajePropina = null;
	BigDecimal EstacionamientoPorSolicitud = null;
	BigDecimal AtencionAClientesPorSolicitud = null;
	BigDecimal TalachasPorSolicitud = null;
	BigDecimal NoDeduciblesPorSolicitud = null;
	BigDecimal TaxisPorSolicitud = null;
	
	BigDecimal totalSolNoDeducible = null;
	BigDecimal totalSolAlimientos = null;
	//BigDecimal totalSolPropina = null;
	BigDecimal totalSolEstacionamiento = null;
	BigDecimal totalSolTaxis = null;
	BigDecimal totalSolMantenimiento = null;
	BigDecimal totalHospedaje = null;
	BigDecimal totalAtencionAClientes = null;
	
	BigDecimal diasPermitidosCarga = null;
	BigDecimal topeAlimentoSAT = null;
	BigDecimal topeAlimentoSATExtranjero = null;
	BigDecimal topeNoDeducibleAnual = null;
	BigDecimal PorcentajeNoDeduciblePorSolicitud = null;
	BigDecimal maximoHospedajeExtranjero = null;

	Logger log = LoggerFactory.getLogger(AvisoService.class);

	@Autowired
	private NivelesPort nivPort;

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private ConfiguracionesPort confPort;

	@Autowired
	private ComprobanteViaticoJPA compJpa;

	@Autowired
	private CfdiPort cfdiPort;

	@Override
	public List<Aviso> obtenerAvisosDeSolicitud(int numeroSolicitud) {

		List<Aviso> lista = new ArrayList<>();
		String nivelUs = "";
		int nivelUsInt = 0;

		// Obtener datos de solicitud
		SolicitudViaticosEntity sv = solUsPort.obtenerSolicitudJPA(numeroSolicitud);

		log.info("solicitud de viatico:" + sv);
		if (sv != null) {

			Usuario user = new Usuario();
			// Busca en tabla Tempus Nu3
			user = usuariosPort.encontrarUsuarioIdJPA(sv.getUsuario());
			if (user.getUsuario() == null) {
				// Busca en tabla Tempus Accesos
				user = usuariosPort.encontrarUsuarioTempusAccesos(sv.getUsuario());
			}

			nivelUs = user.getNivel();
			//nivelUs = "4";
			log.info("Nivel us:" + nivelUs);

			if (nivelUs == null) {
				agregarAWarnings(lista, 1,"","","Usuario sin nivel configurado");
			} else {
				nivelUsInt = Integer.parseInt(nivelUs.trim());
			}	
			
			//Obtener total comprobado
			BigDecimal totalComp = cfdiPort.totalComprobado(numeroSolicitud);
			
			// Validacion estacionamiento por solicitud
			agregarAWarnings(lista, totalComp.compareTo(sv.getAnticipo()),
					"El total " + UtilidadesAdapter.formatNumber(totalComp)
							+ " de comprobación no a exedido lo solicitado "
							+ UtilidadesAdapter.formatNumber(sv.getAnticipo()),
					"El total " + UtilidadesAdapter.formatNumber(totalComp) + " de comprobación excedio lo solicitado "
							+ UtilidadesAdapter.formatNumber(sv.getAnticipo()),
					"Comprobación vs Solicitado");
			
			// Obtener nivel de usuario de la solicitud
			NivelTopeUsuarioEntity nivel = nivPort
						.obtenerNivelPorNivel(nivelUsInt);
	
			// Validar si no se encuentra nivel
			log.info("Topes nivel:" + nivel);
			
			if (nivel != null) {

				DesayunoPorDia = nivel.getTotalAlimentosDesayunoDia() != null ? nivel.getTotalAlimentosDesayunoDia()
						: new BigDecimal("0.00");
				ComidaPorDia = nivel.getTotalAlimentosComidaDia() != null ? nivel.getTotalAlimentosComidaDia()
						: new BigDecimal("0.00");
				CenaPorDia = nivel.getTotalAlimentosCenaDia() != null ? nivel.getTotalAlimentosCenaDia()
						: new BigDecimal("0.00");
				HospedajePorDia = nivel.getHospedajeDia() != null ? nivel.getHospedajeDia() : new BigDecimal("0.00");
				PorcentajePropina = nivel.getPropina() != null ? nivel.getPropina() : new BigDecimal("0.00");
				EstacionamientoPorSolicitud = nivel.getEstacionamiento() != null ? nivel.getEstacionamiento()
						: new BigDecimal("0.00");
				AtencionAClientesPorSolicitud = nivel.getAtencionClientes() != null ? nivel.getAtencionClientes()
						: new BigDecimal("0.00");
				TalachasPorSolicitud = nivel.getTalachas() != null ? nivel.getTalachas() : new BigDecimal("0.00");
				NoDeduciblesPorSolicitud = nivel.getNoDeducibles() != null ? nivel.getNoDeducibles()
						: new BigDecimal("0.00");
				TaxisPorSolicitud = nivel.getTaxis() != null ? nivel.getTaxis() : new BigDecimal("0.00");

				List<ConfiguracionEntity> configs = confPort.obtenerConfiguraciones();

				diasPermitidosCarga = configs.get(0) != null ? new BigDecimal(configs.get(0).getValor1())
						: BigDecimal.ZERO;
				topeAlimentoSAT = configs.get(1) != null ? new BigDecimal(configs.get(1).getValor1()) : BigDecimal.ZERO;

				topeAlimentoSATExtranjero = configs.get(2) != null ? new BigDecimal(configs.get(2).getValor1())
						: BigDecimal.ZERO;
				topeNoDeducibleAnual = configs.get(3) != null ? new BigDecimal(configs.get(3).getValor1())
						: BigDecimal.ZERO;
				PorcentajeNoDeduciblePorSolicitud = configs.get(4) != null ? new BigDecimal(configs.get(4).getValor1())
						: BigDecimal.ZERO;
				maximoHospedajeExtranjero = configs.get(5) != null ? new BigDecimal(configs.get(5).getValor1())
						: BigDecimal.ZERO;

//				totalSolNoDeducible = cfdiPort.totalPorTipoSubCuenta("503-7-3", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-7-3", numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalSolAlimientos = cfdiPort.totalPorTipoSubCuenta("503-2-2", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-2-2", numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalSolPropina = cfdiPort.totalPropina(numeroSolicitud) != null
//						? cfdiPort.totalPropina(numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalSolEstacionamiento = cfdiPort.totalPorTipoSubCuenta("503-2-7-1", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-2-7-1", numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalSolTaxis = cfdiPort.totalPorTipoSubCuenta("503-2-5", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-2-5", numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalSolMantenimiento = cfdiPort.totalPorTipoSubCuenta("503-3-2", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-3-2", numeroSolicitud)
//						: new BigDecimal("0.00");
//				totalHospedaje = cfdiPort.totalPorTipoSubCuenta("503-3-2", numeroSolicitud) != null
//						? cfdiPort.totalPorTipoSubCuenta("503-3-2", numeroSolicitud)
//						: new BigDecimal("0.00");
						
				
				totalSolNoDeducible = cfdiPort.totalPorNombre("NO DEDUCIBLE", numeroSolicitud);
				//Alimentos desayuno, comida, cena
				totalSolAlimientos = cfdiPort.totalPorNombre("DESAYUNO", numeroSolicitud);
				totalSolAlimientos = totalSolAlimientos.add(cfdiPort.totalPorNombre("COMIDA", numeroSolicitud));
				totalSolAlimientos = totalSolAlimientos.add(cfdiPort.totalPorNombre("DESAYUNO", numeroSolicitud));
				//totalSolPropina = cfdiPort.totalPropina(numeroSolicitud);
				totalSolEstacionamiento = cfdiPort.totalPorNombre("ESTACIONAMIENTO", numeroSolicitud);
				totalAtencionAClientes = cfdiPort.totalPorNombre("ATENCION A CLIENTES", numeroSolicitud);
				totalSolTaxis = cfdiPort.totalPorNombre("TAXI", numeroSolicitud);
				totalSolMantenimiento = cfdiPort.totalPorNombre("MANTENIMIENTO", numeroSolicitud);
				totalHospedaje = cfdiPort.totalPorNombre("HOSPEDAJE", numeroSolicitud);

				log.info("\n\n----------------------------------------\n" + "Nivel\n"
						+ "----------------------------------------\n" 
						+ "DesayunoPorDia:" + DesayunoPorDia+ "\n" 
						+ "ComidaPorDia:" + ComidaPorDia+ "\n" 
						+ "CenaPorDia:" + CenaPorDia+ "\n" 
						+ "HospedajePorDia:" + HospedajePorDia + "\n" 
						+ "EstacionamientoPorSolicitud:" + EstacionamientoPorSolicitud+ "\n" 
						+ "AtencionAClientesPorSolicitud:" + AtencionAClientesPorSolicitud + "\n"
						+ "TalachasPorSolicitud:" + TalachasPorSolicitud + "\n" 
						+ "NoDeduciblesPorSolicitud:"+ NoDeduciblesPorSolicitud + "\n" + "TaxisPorSolicitud:" + TaxisPorSolicitud + "\n"
						+ "----------------------------------------\n");

				log.info("\n\n----------------------------------------\n" + "Configuraciones\n"
						+ "----------------------------------------\n" + "total diasPermitidosCarga:"
						+ diasPermitidosCarga + "\n" + "total topeAlimentoSAT:" + topeAlimentoSAT + "\n"
						+ "total topeAlimentoSATExtranjero:" + topeAlimentoSATExtranjero + "\n"
						+ "total topeNoDeducibleAnual:" + topeNoDeducibleAnual + "\n"
						+ "total PorcentajeNoDeduciblePorSolicitud:" + PorcentajeNoDeduciblePorSolicitud + "\n"
						+ "total maximoHospedajeExtranjero:" + maximoHospedajeExtranjero + "\n"
						+ "----------------------------------------\n");

				log.info("\n\n----------------------------------------\n" 
				+ "Totales solicitud\n"
						+ "----------------------------------------\n" 
				+ "total totalSolNoDeducible:"	+ totalSolNoDeducible + "\n" 
						+ "total totalSolAlimientos:" + totalSolAlimientos + "\n"
						+ "total totalSolEstacionamiento:"+ totalSolEstacionamiento + "\n" 
						+ "total totalAtencionAClientes:"+ totalAtencionAClientes + "\n" 
						+ "total totalSolTaxis:" + totalSolTaxis + "\n"
						+ "total totalSolMantenimiento:" + totalSolMantenimiento + "\n"
						+ "total hospedaje:" + totalHospedaje + "\n"
						+ "----------------------------------------");
				
				//Saber que dia es permitido de carga
				Date fechaPermitidaCarga = UtilidadesAdapter.sumarDiasAFecha(sv.getFechaInicio(), diasPermitidosCarga.intValue());
				
				validacionFechasEnDia(lista, numeroSolicitud,fechaPermitidaCarga,PorcentajePropina);
				
				// Se toma el periodo y con el ejercicio actual
				Date d = new Date();
				String fechaCadena = UtilidadesAdapter.formatearFecha(d);
				BigDecimal noDeducibleAnio = cfdiPort.totalNoDeduciblePorAnio(fechaCadena.substring(0, 4), sv.getUsuario());
				
				log.info("Monto deducible por anio de usuario" + noDeducibleAnio + " no deducible por anio permitido:" + topeNoDeducibleAnual);
				// Validacion porcentaje no deducible
				agregarAWarnings(lista, noDeducibleAnio.compareTo(topeNoDeducibleAnual),
						"El monto no deducible anual de la solicitud es " +UtilidadesAdapter.formatNumber(noDeducibleAnio)+ " no ha excedido el tope permitido " +UtilidadesAdapter.formatNumber(topeNoDeducibleAnual),
						"El monto no deducible anual de la solicitud es "+UtilidadesAdapter.formatNumber(noDeducibleAnio)+" ha excedido el permitido de " + UtilidadesAdapter.formatNumber(topeNoDeducibleAnual),
						"Monto no deducible anual permitido");
				
				// Porcentaje no deducible
				BigDecimal porcentajeNoDeducible = PorcentajeNoDeduciblePorSolicitud.multiply(sv.getAnticipo())
						.divide(new BigDecimal("100"));
				
				log.info("Monto deducible del " + PorcentajeNoDeduciblePorSolicitud + "%:" + porcentajeNoDeducible);
				// Validacion porcentaje no deducible
				agregarAWarnings(lista, totalSolNoDeducible.compareTo(porcentajeNoDeducible),
						"El porcentaje del monto no deducible esta correcto",
						"El porcentaje de monto no deducible se esta exediendo del " + PorcentajeNoDeduciblePorSolicitud
								+ "%",
						"No deducible vs porcentaje permitido por Solicitud");

				// Validacion no deducible por solicitud
				agregarAWarnings(lista, totalSolNoDeducible.compareTo(NoDeduciblesPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolNoDeducible)
								+ " de no deducible por la solicitud no a exedido el permitido "
								+ UtilidadesAdapter.formatNumber(NoDeduciblesPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolNoDeducible) + " de no dedubile exede el monto permitido de "
								+ UtilidadesAdapter.formatNumber(NoDeduciblesPorSolicitud),
						"No deducible por solicitud");

				// Validacion taxis por solicitud
				agregarAWarnings(lista, totalSolTaxis.compareTo(TaxisPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolTaxis) + " de taxis por la solicitud no a exedido el permitdo "
								+ UtilidadesAdapter.formatNumber(TaxisPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolTaxis) + " de taxis exede el monto permitido de " 
								+ UtilidadesAdapter.formatNumber(TaxisPorSolicitud),
						"Taxis por solicitud");

				// Validacion estacionamiento por solicitud
				agregarAWarnings(lista, totalSolEstacionamiento.compareTo(EstacionamientoPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolEstacionamiento)
								+ " de estacionamiento por la solicitud no a exedido el permitdo "
								+ UtilidadesAdapter.formatNumber(EstacionamientoPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolEstacionamiento) + " de estacionamiento exede el monto permitido de "
								+ UtilidadesAdapter.formatNumber(EstacionamientoPorSolicitud),
						"Total estacionamiento por solicitud");
				
				// Validacion atencion a clientes por solicitud
				agregarAWarnings(lista, totalAtencionAClientes.compareTo(AtencionAClientesPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalAtencionAClientes)
								+ " de atención a clientes por la solicitud no a exedido el permitdo "
								+ UtilidadesAdapter.formatNumber(AtencionAClientesPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalAtencionAClientes) + " de estacionamiento exede el monto permitido de "
								+ UtilidadesAdapter.formatNumber(AtencionAClientesPorSolicitud),
						"Total estacionamiento por solicitud");
				
				// Validacion mantenimiento por solicitud
				agregarAWarnings(lista, totalSolMantenimiento.compareTo(TalachasPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolMantenimiento) + " de mantenimiento por la solicitud no a exedido el permitido "
								+ UtilidadesAdapter.formatNumber(TalachasPorSolicitud),
						"El total " + UtilidadesAdapter.formatNumber(totalSolMantenimiento) + " de mantenimiento	 exede el monto permitido de "
								+ UtilidadesAdapter.formatNumber(TalachasPorSolicitud),
						"Mantenimiento por solicitud");
				
				// Validacion tope de hospedaje SAT
				if (totalHospedaje.compareTo(BigDecimal.ZERO) == 1) {
					agregarAWarningsFecha(lista, totalHospedaje.compareTo(maximoHospedajeExtranjero),
							"El total " + UtilidadesAdapter.formatNumber(totalHospedaje) + " de hospedaje permitido excede "
									+ "el monto permitido de " + UtilidadesAdapter.formatNumber(maximoHospedajeExtranjero),
							"Monto permitido de hospedaje extranjero","E");
				}

			}
			
		}
		return lista;
	}
	
	private void validacionFechasEnDia(List<Aviso> lista,int numeroSolicitud,Date fechaPermitidaCarga,BigDecimal porcentajePropina) {
		
		//log.info("fecha permitida carga:"+fechaPermitidaCarga);
		
		//Tomando fechas de los comprobantes
		List<String> fechasComp = compJpa.findFechasComprobantes(numeroSolicitud);
		log.info("Fechas comprobantes:" + fechasComp);

		BigDecimal monto = BigDecimal.ZERO;
		BigDecimal propinaBD = BigDecimal.ZERO;
		BigDecimal totalComidas = BigDecimal.ZERO;
		
		@SuppressWarnings("unused")
		String codigoQ = "";
		String montoQ = "";
		String monedaQ = "";
		String tipoGastoQ = "";
		String propinaQ = "";
		String fechaF = "";
		@SuppressWarnings("unused")
		String moneda = "";
		
		boolean wFechaCarga = false;
		
		for (String fecha : fechasComp) {
			
			log.info("Revisando fecha:" + fecha);

			List<String> montosFecha;

			try {

				// Tomar totales con su codigo de subcunta y moneda para validaciones
				montosFecha = cfdiPort.totalesDeFecha(UtilidadesAdapter.cadenaAFecha(fecha), numeroSolicitud);

				// Total comida por dia
				totalComidas = cfdiPort.totalPorNombreSubCuentaYFecha("DESAYUNO", numeroSolicitud,
						UtilidadesAdapter.cadenaAFecha(fecha));
				totalComidas = totalComidas.add(cfdiPort.totalPorNombreSubCuentaYFecha("COMIDA", numeroSolicitud,
						UtilidadesAdapter.cadenaAFecha(fecha)));
				totalComidas = totalComidas.add(cfdiPort.totalPorNombreSubCuentaYFecha("CENA", numeroSolicitud,
						UtilidadesAdapter.cadenaAFecha(fecha)));

				// Validacion alimentos tope por dia del SAT
				if (totalComidas.compareTo(BigDecimal.ZERO) == 1) {
					log.info("Total comidas del día " + fecha + " " + totalComidas);
					agregarAWarningsFecha(lista, totalComidas.compareTo(topeAlimentoSAT),
							"El total " + UtilidadesAdapter.formatNumber(totalComidas) + " de alimentos del día ante el SAT "
									+ fechaF + " excede el monto permitido de "
									+ UtilidadesAdapter.formatNumber(topeAlimentoSAT),
							"Monto permitido alimientos SAT en fecha " + fechaF, "E");
				}

				// Realizar validaciones pora cada fecha
				for (String montoycodigo : montosFecha) {

					log.info("Fecha permitida de carga:" + fechaPermitidaCarga);
					// Esta validacion solo se hace una vez si alguno de los comprobantes excede la
					// fecha
					if (!wFechaCarga) {
						log.info("Comparacion fechas:"
								+ UtilidadesAdapter.cadenaAFecha(fecha).compareTo(fechaPermitidaCarga));
						if (UtilidadesAdapter.cadenaAFecha(fecha).compareTo(fechaPermitidaCarga) > 0) {
							Aviso a = new Aviso();
							a.setTitulo("Fecha carga permitida excedida");
							a.setTipo("W");
							a.setMensaje("La fecha limite de carga es "
									+ UtilidadesAdapter.formatearFecha(fechaPermitidaCarga)
									+ ", algunos comprobantes han excedido esta fecha");
							lista.add(a);
							wFechaCarga = true;
						}
					}

					// Tomando valores ya que estan divididos por coma
					List<String> MontoYCodigoArray = Arrays.asList(montoycodigo.split(","));
					log.info(MontoYCodigoArray + "");
					montoQ = MontoYCodigoArray.get(0);
					codigoQ = MontoYCodigoArray.get(1);
					monedaQ = MontoYCodigoArray.get(2);
					tipoGastoQ = MontoYCodigoArray.get(3);
					propinaQ = MontoYCodigoArray.get(4);

					if (!montoQ.equals("null")) {
						monto = new BigDecimal(montoQ);
					} else {
						monto = BigDecimal.ZERO;
					}
					if (!monedaQ.equals("null")) {
						moneda = MontoYCodigoArray.get(2);
					} else {
						moneda = "";
					}
				
					log.info("propina q"+propinaQ);
					propinaBD = new BigDecimal(propinaQ);
					

					// Formatear fecha obtenida
					fechaF = fecha.substring(0, 10);

					// log.info("Monto :" + monto + " codigo " + codigo);

					// Validacion tope por dia de alimentos
					//if (codigoQ.equals("503-2-2")) {
						String tipoGastoUC = tipoGastoQ.toUpperCase();
						log.info(tipoGastoUC);
						if (tipoGastoUC.contains("DESAYUNO")) {
							agregarAWarningsFecha(lista, monto.compareTo(DesayunoPorDia),
									"El total " + UtilidadesAdapter.formatNumber(monto) + " de DESAYUNOS del día "
											+ fechaF + " excede el monto permitido de "
											+ UtilidadesAdapter.formatNumber(DesayunoPorDia),
									"Monto permitido DESAYUNOS en fecha " + fechaF, "W");
						}
						if (tipoGastoUC.contains("COMIDA")) {
							agregarAWarningsFecha(lista, monto.compareTo(ComidaPorDia),
									"El total " + UtilidadesAdapter.formatNumber(monto) + " de COMIDA del día " + fechaF
											+ " excede el monto permitido de "
											+ UtilidadesAdapter.formatNumber(ComidaPorDia),
									"Monto permitido COMIDA en fecha " + fechaF, "W");
						}
						if (tipoGastoUC.contains("CENA")) {
							agregarAWarningsFecha(lista, monto.compareTo(CenaPorDia),
									"El total " + UtilidadesAdapter.formatNumber(monto) + " de CENA del día " + fechaF
											+ " excede el monto permitido de "
											+ UtilidadesAdapter.formatNumber(CenaPorDia),
									"Monto permitido CENA en fecha " + fechaF, "W");
						}
						
						
						if(propinaBD.compareTo(BigDecimal.ZERO) == 1) {
							// Validar si el monto de factura excede
							BigDecimal porcentajePropinaF = BigDecimal.ZERO;
							porcentajePropinaF = porcentajePropina.divide(new BigDecimal("100"));
							BigDecimal montoPermitidoPropina = porcentajePropinaF.multiply(monto);
							log.info("monto permitido propina:"+montoPermitidoPropina);
							log.info("monto  propina:"+propinaBD);
							log.info("Comparacion monto permitido propina con monto:"+ 
							propinaBD.compareTo(montoPermitidoPropina));
							agregarAWarningsFecha(lista,propinaBD.compareTo(montoPermitidoPropina),
									"El monto " + UtilidadesAdapter.formatNumber(propinaBD)
											+ " de propina del día " + fechaF + " excede el monto permitido de "
											+ UtilidadesAdapter.formatNumber(montoPermitidoPropina),
									"Monto permitido PROPINA en fecha " + fechaF, "W");
						}
						
					//}
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private void agregarAWarnings(List<Aviso> lista, int valorComparacion, String MensajeExitoso, String MensajeNoExitoso,
			String titulo) {
		Aviso a = new Aviso();
		a.setTitulo(titulo);
		if (valorComparacion == 1) {
			a.setTipo("W");
			a.setMensaje(MensajeNoExitoso);
			lista.add(a);
		} else {
			a.setTipo("S");
			a.setMensaje(MensajeExitoso);
			lista.add(a);
		}
	}
	
	private void agregarAWarningsFecha(List<Aviso> lista, int valorComparacion, String Mensaje,
			String titulo,String tipo) {
		Aviso a = new Aviso();
		a.setTitulo(titulo);
		if (valorComparacion == 1) {
			a.setTipo(tipo);
			a.setMensaje(Mensaje);
			lista.add(a);
		}
	}

}
