package com.viaticos.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.adapter.out.cfdi.CfdiAdapter;
import com.viaticos.application.port.in.PrepolizaUseCase;
import com.viaticos.application.port.out.ArchivosPort;
import com.viaticos.application.port.out.CecoPort;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.SolicitudesDeUsuarioPort;
import com.viaticos.application.port.out.SubCuentasContablesPort;
import com.viaticos.domain.Cfdi;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.SolicitudViaticosEntity;
import com.viaticos.domain.SubCuentaContableEntity;
import com.viaticos.domain.sql.nu3.CecoEntity;

@Service
@PropertySource(value = "classpath:configuraciones-fox.properties")
public class PrepolizaService implements PrepolizaUseCase {

	Logger log = LoggerFactory.getLogger(Prepoliza.class);

	@Autowired
	private SolicitudesDeUsuarioPort solUsPort;

	@Autowired
	private CfdiPort cfdiPort;

	@Autowired
	private EmpresaPort empPort;

	@Autowired
	private SubCuentasContablesPort subCuenta;

	@Autowired
	private CecoPort cecoPort;

	@Autowired
	private ArchivosPort archivosPort;

	@Override
	public List<Prepoliza> generarPoliza(int numeroSolicitud, boolean tabla) {
		List<Prepoliza> salida = new ArrayList<Prepoliza>();
		SolicitudViaticosEntity solicitud = new SolicitudViaticosEntity();
		solicitud = solUsPort.obtenerSolicitudJPA(numeroSolicitud);
		if (solicitud == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró solicitud, " + numeroSolicitud);

		List<ComprobanteViaticoEntity> comprobantes = solicitud.getComprobanteViaticosEntity();

		salida = generarPolizaDetalle(comprobantes, solicitud, tabla);

		return salida;
	}

	@Override
	public List<Prepoliza> generarPolizaDetalle(List<ComprobanteViaticoEntity> comprobantes,
			SolicitudViaticosEntity solicitud, boolean tabla) {

		List<Prepoliza> salida = new ArrayList<Prepoliza>();
		Prepoliza p = null;

		String fecha = UtilidadesAdapter.obtenerFechaActual();
		String concepto = "";
		String metodoPago = "";
		String formaPago = "";
		String metodoPagoDesc = "";
		String formaPagoDesc = "";
		String uuid = "";
		String rfc = "";
		String empresaSolicitud = "";
		String descSubCuenta = "";

		BigDecimal gastosDiversos = BigDecimal.ZERO;
		BigDecimal descuentos = BigDecimal.ZERO;
		BigDecimal totalAbono = BigDecimal.ZERO;
		BigDecimal totalCargo = BigDecimal.ZERO;

		descuentos = cfdiPort.totalNoAplica(solicitud.getId(), true);
		String centcost = "";
		if (solicitud.getCeco() != null) {
			centcost = solicitud.getCeco().trim();
		}

		if (solicitud.getEmpresa() != null) {
			empresaSolicitud = solicitud.getEmpresa().trim();
		}

		// Obtener texto de ceco
		String textoCeco = "";
		CecoEntity ce = cecoPort.encontrarTextoDeCeco(centcost);
		if (ce != null) {
			textoCeco = ce.getTexto();
		}

		EmpresaEntity empresa = empPort.obtenerEmpresaPorEmpresa(empresaSolicitud);
		String tipoGasto = "";

		if (empresa != null) {
			log.info("Se generará poliza para  " + empresa.getEmpresa() + " en sistema " + empresa.getSistema());

			int count = 1;
			for (ComprobanteViaticoEntity comp : comprobantes) {

				SubCuentaContableEntity sucuentaExc = subCuenta.obtenerSubCuentaC(
						comp.getSub_cuenta_contable().getCodigo(), solicitud.getEmpresa(), centcost, "EXCEDENTE");

				descSubCuenta = comp.getSub_cuenta_contable().getDescripcion();

				if (sucuentaExc != null) {
					// Abono a empleado
					BigDecimal totalReintegro = cfdiPort.totalPorTipoSubCuenta(sucuentaExc.getCodigo(),
							solicitud.getId());
					tipoGasto = "FALTA";
					p = new Prepoliza(fecha, "3", solicitud.getCuentaContable(), "", "", "", BigDecimal.ZERO,
							totalReintegro, centcost, textoCeco, "Deudores diversos", "" + count, tipoGasto);
					salida.add(p);
					totalAbono = totalAbono.add(totalReintegro);

					// Cargo banco
					p = new Prepoliza(fecha, "3", solicitud.getCuentaContable(), "", "", "", totalReintegro,
							BigDecimal.ZERO, centcost, textoCeco, "Bancos", "" + count, tipoGasto);
					salida.add(p);
					totalCargo = totalCargo.add(totalReintegro);

				} else {

					if (comp.getCfdiEntity() != null) {
						metodoPago = comp.getCfdiEntity().getMetodoPago() != null ? comp.getCfdiEntity().getMetodoPago()
								: "";
						metodoPagoDesc = CfdiAdapter.metodoPago(metodoPago);
						formaPago = comp.getCfdiEntity().getFormaPago() != null ? comp.getCfdiEntity().getFormaPago()
								: "";
						formaPagoDesc = CfdiAdapter.tipoPago(formaPago);
						uuid = comp.getCfdiEntity().getUuid() != null ? comp.getCfdiEntity().getUuid() : "";
						rfc = comp.getCfdiEntity().getRfcEmisor() != null ? comp.getCfdiEntity().getRfcEmisor() : "";

						if (empresa.getSistema().equals("SAPB1")) {
							concepto = descSubCuenta;
						} else {
							concepto = metodoPago + "-" + metodoPagoDesc + " " + formaPago + "-" + formaPagoDesc + " "
									+ solicitud.getNombreCompletoUsuario() + " Núm. solicitud:" + solicitud.getId();
						}
					} else {
						metodoPago = "";
						formaPago = "";
						uuid = "";
						rfc = "";
						if (empresa.getSistema().equals("SAPB1")) {
							concepto = descSubCuenta;
						} else {
							concepto = solicitud.getNombreCompletoUsuario();
						}
					}

					String subCuentaComp = "";

					BigDecimal total = new BigDecimal("0.00");
					BigDecimal importeSinIimp = new BigDecimal("0.00");
					boolean importeSinIimpB = false;

					BigDecimal iva = new BigDecimal("0.00");
					BigDecimal ish = new BigDecimal("0.00");
					BigDecimal isr = new BigDecimal("0.00");
					BigDecimal ieps = new BigDecimal("0.00");

					BigDecimal ivaR = new BigDecimal("0.00");
					BigDecimal isrR = new BigDecimal("0.00");
					BigDecimal iepsR = new BigDecimal("0.00");
					BigDecimal iva16 = new BigDecimal("0.00");
					BigDecimal tasaIva16 = new BigDecimal("0.00");

					if (!comp.getRutaXml().equals("")) {

						total = total.add(new BigDecimal(comp.getCfdiEntity().getTotal())).setScale(3,
								RoundingMode.DOWN);

						importeSinIimp = importeSinIimp.add(new BigDecimal(comp.getCfdiEntity().getTotal()));

						if (comp.getId() == 204) {
							System.out.print(true);
						}

						iva = iva.add(new BigDecimal(comp.getCfdiEntity().getIva())).setScale(3, RoundingMode.DOWN);
						ish = ish.add(new BigDecimal(comp.getCfdiEntity().getIsh())).setScale(3, RoundingMode.DOWN);
						isr = isr.add(new BigDecimal(comp.getCfdiEntity().getIsr())).setScale(3, RoundingMode.DOWN);
						ieps = ieps.add(new BigDecimal(comp.getCfdiEntity().getIeps()).setScale(3, RoundingMode.DOWN));

						importeSinIimp = importeSinIimp.subtract(iva);
						importeSinIimp = importeSinIimp.subtract(isr);
						importeSinIimp = importeSinIimp.subtract(ieps);
						importeSinIimp = importeSinIimp.subtract(ish);

						ivaR = ivaR.add(new BigDecimal(comp.getCfdiEntity().getIvaRetenido()));
						isrR = isrR.add(new BigDecimal(comp.getCfdiEntity().getIsrRetenido()));
						iepsR = iepsR.add(new BigDecimal(comp.getCfdiEntity().getIepsRetenido()));

						subCuentaComp = comp.getSub_cuenta_contable().getCodigo();
						tipoGasto = comp.getSub_cuenta_contable().getTipoGasto();

						Cfdi cfdi = null;
						if (empresa.getSistema().equals("SAPB1")) {

							String ruta = comp.getRutaXml();
							// String ruta = "\\69bada3d-4a44-4d64-b9e9-786b12e8a3ec.xml";
							byte[] xml = null;
							xml = archivosPort.obtenerArchivo(ruta);
							cfdi = cfdiPort.validarCfdi(xml);

							if (cfdi.getIva16() != null) {
								iva16 = iva16.add(cfdi.getIva16());
							}

							if (cfdi.getTasaIva16() != null) {
								tasaIva16 = tasaIva16.add(cfdi.getTasaIva16());
							}

							// Validar si tiene iva0 para hacer calculo
							if (cfdi.isTieneIVA0()) {
								if (cfdi.getIva16() != null) {
									BigDecimal importeSinIimpI = BigDecimal.ZERO;
									importeSinIimpI = importeSinIimpI.add(iva16);
									importeSinIimpI = importeSinIimpI.divide(cfdi.getTasaIva16());
									p = new Prepoliza(fecha, "3", subCuentaComp, concepto, uuid, rfc,
											importeSinIimpI.setScale(3, RoundingMode.DOWN), BigDecimal.ZERO, centcost,
											textoCeco, "Importe sin iva", "" + count, tipoGasto);
									salida.add(p);
									totalCargo = totalCargo.add(importeSinIimpI.setScale(3, RoundingMode.DOWN));
								}else {
									System.out.println("Tiene iva 0 pero no tiene iva 16");
								}
							} else {
								// Gastos de venta importe sin iva
								if (importeSinIimp.compareTo(BigDecimal.ZERO) == 1) {
									importeSinIimpB = true;
								}
							}
						} else {
							// Gastos de venta importe sin iva
							if (importeSinIimp.compareTo(BigDecimal.ZERO) == 1) {
								importeSinIimpB = true;
							}
						}

						// Se ingresa el importe sin impuesto si paso validacion anterior
						if (importeSinIimpB) {
							p = new Prepoliza(fecha, "3", subCuentaComp, concepto, uuid, rfc,
									importeSinIimp.setScale(3, RoundingMode.DOWN), BigDecimal.ZERO, centcost, textoCeco,
									"Importe sin iva", "" + count, tipoGasto);
							salida.add(p);
							totalCargo = totalCargo.add(importeSinIimp.setScale(3, RoundingMode.DOWN));
						}

						// Gastos IVA pagado
						SubCuentaContableEntity sucuentaiva = subCuenta.obtenerSubCuentaPorTipo(empresa.getEmpresa(),
								centcost, "IVA");
						
						if (iva.compareTo(BigDecimal.ZERO) == 1) {
							if (sucuentaiva != null) {

								if (empresa.getSistema().equals("SAPB1")) {

									BigDecimal calculo = BigDecimal.ZERO;
									BigDecimal calculoIVA16 = BigDecimal.ZERO;

									// Agregar el iva 16 solamente
									p = new Prepoliza(fecha, "3", sucuentaiva.getCodigo(), concepto, uuid, rfc, iva16,
											BigDecimal.ZERO, "", "", "IVA", "" + count, tipoGasto);
									salida.add(p);
									totalCargo = totalCargo.add(iva);
									
									if (cfdi.getIva16() != null) {
										calculoIVA16 = calculoIVA16.add(iva16);
										calculoIVA16 = calculoIVA16.divide(tasaIva16);
									}

									if (cfdi.isTieneIVA0()) {
										SubCuentaContableEntity sucuentaiva0 = subCuenta
												.obtenerSubCuentaPorTipo(empresa.getEmpresa(), centcost, "IVA0");
										if (sucuentaiva0 != null) {

											calculo = importeSinIimp.subtract(calculoIVA16);
											tipoGasto = sucuentaiva0.getTipoGasto();
											p = new Prepoliza(fecha, "3", sucuentaiva0.getCodigo(), concepto, uuid, rfc,
													calculo, BigDecimal.ZERO, "", "", "IVA0", "" + count, tipoGasto);
											salida.add(p);
											totalCargo = totalCargo.add(calculo);
										} else {
											// No se encontro la subcuenta
											System.out.println(
													"No se encontro la subcuenta de IVA0 para " + empresa.getEmpresa());
											 throw new NullPointerException("No se encontro la subcuenta de IVA0 para " + empresa.getEmpresa());
										}
										System.out.print("Tiene iva0:" + calculo);
									} else {
										System.out.println("No tiene iva0");
									}

								} else {

									tipoGasto = sucuentaiva.getTipoGasto();
									p = new Prepoliza(fecha, "3", sucuentaiva.getCodigo(), concepto, uuid, rfc, iva,
											BigDecimal.ZERO, "", "", "IVA", "" + count, tipoGasto);
									salida.add(p);
									totalCargo = totalCargo.add(iva);

								}

							} else {
								// No se encontro la subcuenta
								System.out.println("No se encontro la subcuenta de IVA para " + empresa.getEmpresa());
								throw new NullPointerException(
										"No se encontro la subcuenta de IVA para " + empresa.getEmpresa());
							}
						}

						// Gastos ISH pagado
						SubCuentaContableEntity sucuentaish = subCuenta.obtenerSubCuentaPorTipo(empresa.getEmpresa(),
								centcost, "ISH");
						// Gastos ish pagado
						if (ish.compareTo(BigDecimal.ZERO) == 1) {
							if (sucuentaish != null) {
								tipoGasto = sucuentaish.getTipoGasto();
								p = new Prepoliza(fecha, "3", sucuentaish.getCodigo(), concepto, uuid, rfc, ish,
										BigDecimal.ZERO, centcost, textoCeco, "ISH", "" + count, tipoGasto);
								salida.add(p);
								totalCargo = totalCargo.add(ish);
							} else {
								// No se encontro la subcuenta
								System.out.println("No se encontro la subcuenta de ISH para " + empresa.getEmpresa());
								 throw new NullPointerException("No se encontro la subcuenta de ISH para " + empresa.getEmpresa());

							}
						}

						// Gastos ISR pagado
						if (isr.compareTo(BigDecimal.ZERO) == 1) {
							tipoGasto = sucuentaish.getTipoGasto();
							p = new Prepoliza(fecha, "3", subCuentaComp, concepto, uuid, rfc, isr, BigDecimal.ZERO,
									centcost, textoCeco, "ISR", "" + count, tipoGasto);
							salida.add(p);
							totalCargo = totalCargo.add(isr);
						}

						// Gastos IEPS pagado
						SubCuentaContableEntity sucuentaieps = subCuenta.obtenerSubCuentaPorTipo(empresa.getEmpresa(),
								centcost, "IEPS");
						if (ieps.compareTo(BigDecimal.ZERO) == 1) {
							if (sucuentaieps != null) {
								tipoGasto = sucuentaieps.getTipoGasto();
								if (sucuentaieps != null) {
									p = new Prepoliza(fecha, "3", sucuentaieps.getCodigo(), concepto, uuid, rfc, ieps,
											BigDecimal.ZERO, centcost, textoCeco, "IEPS", "" + count, tipoGasto);
									salida.add(p);
									totalCargo = totalCargo.add(ieps);
								}
							} else {
								// No se encontro la subcuenta
								System.out.println("No se encontro la subcuenta de IEPS para " + empresa.getEmpresa());
								 throw new NullPointerException("No se encontro la subcuenta de IEPS para " +  empresa.getEmpresa());

							}

						}

						// Abonos
						// Gastos iva retenido
						if (ivaR.compareTo(BigDecimal.ZERO) == 1) {
							tipoGasto = sucuentaiva.getTipoGasto();
							p = new Prepoliza(fecha, "3", sucuentaiva.getCodigo(), concepto, uuid, rfc, BigDecimal.ZERO,
									ivaR, "", "", "IVA RETENIDO", "" + count, tipoGasto);
							salida.add(p);
							totalAbono = totalAbono.add(ivaR);
						}

						// Gastos isr retenido
						if (isrR.compareTo(BigDecimal.ZERO) == 1) {
							tipoGasto = sucuentaiva.getTipoGasto();
							p = new Prepoliza(fecha, "3", sucuentaiva.getCodigo(), concepto, uuid, rfc, BigDecimal.ZERO,
									isrR, "", "", "ISR RETENIDO", "" + count, tipoGasto);
							salida.add(p);
							totalAbono = totalAbono.add(isrR);
						}

						// Gastos ieps pagado
						if (iepsR.compareTo(BigDecimal.ZERO) == 1) {
							tipoGasto = sucuentaiva.getTipoGasto();
							p = new Prepoliza(fecha, "3", sucuentaiva.getCodigo(), concepto, uuid, rfc, BigDecimal.ZERO,
									iepsR, "", "", "IEPS RETENIDO", "" + count, tipoGasto);
							salida.add(p);
							totalAbono = totalAbono.add(iepsR);
						}

						// Gastos deudores diversos
						if (solicitud.getCuentaContable() != null) {
							gastosDiversos = gastosDiversos.add(total);
						}

					} else {

						total = comp.getTotal().setScale(2, RoundingMode.HALF_UP);

						// Reintegro esto no se requirio en dos renglones por eso se comento, solo se
						// agregara lo de bancos
						if (comp.getSub_cuenta_contable().getDescripcion().contains("REINTEGRO")) {
							SubCuentaContableEntity bancos = subCuenta.obtenerSubCuentaPorTipo(empresa.getEmpresa(),
									centcost, "BANCOS");
							if (bancos != null) {
								concepto = "Bancos";
								if (empresa.getSistema().equals("SAPB1")) {
									concepto = "BANCOS";
								}
								if (empresa.getSistema().equals("SYS21")) {
									concepto = "";
								}
								tipoGasto = bancos.getTipoGasto();
								p = new Prepoliza(fecha, "3", bancos.getCodigo(), concepto, "", "", total,
										BigDecimal.ZERO, centcost, textoCeco, "Bancos", "" + count, tipoGasto);
								salida.add(p);
								totalCargo = totalCargo.add(total);
								gastosDiversos = gastosDiversos.add(total);

							}
						} else {
							SubCuentaContableEntity sucuentanod = subCuenta
									.obtenerSubCuentaPorTipo(empresa.getEmpresa(), centcost, "NODED");

							if (sucuentanod != null) {
								tipoGasto = sucuentanod.getTipoGasto();
								p = new Prepoliza(fecha, "3", sucuentanod.getCodigo(), concepto, uuid, rfc, total,
										BigDecimal.ZERO, centcost, textoCeco, "Total no deducible", "" + count,
										tipoGasto);
								salida.add(p);
								totalCargo = totalCargo.add(total);
								// Gastos deudores diversos
								if (solicitud.getCuentaContable() != null) {
									gastosDiversos = gastosDiversos.add(total);
								}
							} else {
								// No se encontro la subcuenta
								System.out.println(
										"No se encontro la subcuenta de no deducible para " + empresa.getEmpresa());
								if (solicitud.getCuentaContable() != null) {
									gastosDiversos = gastosDiversos.add(total);
								}

							}
						}
					}
				}
				count++;
			}

			// Descuentos
			SubCuentaContableEntity sucuentadesc = subCuenta.obtenerSubCuentaPorTipo(empresa.getEmpresa(), centcost,
					"DESC");

			concepto = "Comprobación gastos";
			if (empresa.getSistema().equals("SAPB1")) {
				concepto = "COMPROBACION GASTOS";
			}
			if (empresa.getSistema().equals("SYS21")) {
				concepto = "";
			}

			if (descuentos.compareTo(BigDecimal.ZERO) == 1) {
				if (sucuentadesc != null) {
					tipoGasto = sucuentadesc.getTipoGasto();
					p = new Prepoliza(fecha, "3", sucuentadesc.getCodigo(), concepto, "", "", BigDecimal.ZERO,
							descuentos, "", "", "Comprobación de gastos " + solicitud.getNombreCompletoUsuario()
									+ " Num Solicitud " + solicitud.getId(),
							"" + count, tipoGasto);
					salida.add(p);
					totalAbono = totalAbono.add(descuentos);
					gastosDiversos = gastosDiversos.subtract(descuentos);
					count++;
				} else {
					// No se encontro la subcuenta
					System.out.println("No se encontro la subcuenta de descuentos para " + empresa.getEmpresa());
					//throw new NullPointerException("No se encontro la subcuenta de DESCUENTOS para " + empresa.getEmpresa());
				}
			}

			concepto = "Deudores Diversos";
			if (empresa.getSistema().equals("SAPB1")) {
				concepto = "DEUDORES DIVERSOS";
			}
			if (empresa.getSistema().equals("SYS21")) {
				concepto = "Deudores Diversos";
			}

			// Gastos deudores diversos
			tipoGasto = "FALTA";
			p = new Prepoliza(fecha, "3", solicitud.getCuentaContable(), concepto, "", "", BigDecimal.ZERO,
					gastosDiversos, "", "", "Deudores Diversos", "" + count, tipoGasto);
			salida.add(p);
			totalAbono = totalAbono.add(gastosDiversos);

			if (tabla) {

				// Totales
				p = new Prepoliza("", "", "", "", "", "Totales", totalCargo, totalAbono, "", "", "", "", "");
				salida.add(p);

			}

		} else {
			log.info("No esta dada de alta la empresa");
			throw new NullPointerException("No esta dada de alta la empresa");
		}

		return salida;

	}

}
