package com.viaticos.aplication.port.fox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.in.PrepolizaUseCase;
import com.viaticos.application.port.out.ContabilizarFoxPort;
import com.viaticos.domain.ComprobanteViaticoEntity;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.SolicitudViaticosEntity;

@Service
@Repository
@PropertySource(value = "classpath:configuraciones-viaticos.properties")
@PropertySource(value = "classpath:configuraciones-fox.properties")
public class ContabilizarFoxRepository implements ContabilizarFoxPort {

	static Logger log = LoggerFactory.getLogger(ContabilizarFoxRepository.class);

	public StringBuilder pd = null;

	public int nPoliza;

	@Value("${ambiente}")
	private String ambiente;
	
	@Value("${ruta.bdfoxqas}")
	private String rutaBdFoxQAS;
	
	@Value("${ruta.bdfoxpro}")
	private String rutaBdFoxPRO;

	@Value("${bd.cabecera}")
	private String bdCabecera;

	@Value("${bd.comprobantes}")
	private String bdComprobantes;

	@Value("${bd.nacionales}")
	private String bdNacionales;

	@Value("${bd.doctos}")
	private String bdDoctos;

	@Autowired
	private PrepolizaUseCase prepoPort;

	@Override
	public Object insertarEncabezado(SolicitudViaticosEntity solicitud, int numPoliza) {

		nPoliza = numPoliza;

		pd = new StringBuilder();

		// contarValores(rutaBdFox + bdCabecera);

		// Se crea el objeto writer
		// DBFWriter writer = new DBFWriter(new File(rutaBdFox + bdCabecera));
		// Se agregan los datos
		Object[] rowData = new Object[21];
		rowData[0] = "3"; // Tipo
		rowData[1] = numPoliza;// Consecutivo
		Date d = UtilidadesAdapter.formatearDateaDate(new Date(), "dd/MM/yyyy");
		String date = UtilidadesAdapter.formatearFechaDDMMYYYY(d);
		rowData[2] = date;// d; // Fecha carga
		rowData[3] = 0; // Ref pol
		rowData[4] = ""; // Ban
		rowData[5] = ""; // Cta ori
		rowData[6] = ""; // Ban des
		rowData[7] = ""; // Cta des

		Date d8 = UtilidadesAdapter.formatearDateaDate(solicitud.getFechaCreacion(), "dd/MM/yyyy");
		String dateFechaEntrega = UtilidadesAdapter.formatearFechaDDMMYYYY(d8);
		rowData[8] = dateFechaEntrega; // Fecha en que
										// se entrega
		rowData[9] = "Comprobación de gastos " + solicitud.getNombreCompletoUsuario() + " Num Solicitud " + solicitud.getId(); // Concepto
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
		rowData[19] = "0000-00-00"; // Fecha mod
		rowData[20] = ""; // Hora mod

		// Regresa solo el objeto rowData
		// writer.addRecord(rowData);
		// writer.close();

		pd.append("\n");
		pd.append(
				"----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		pd.append("\n");
		pd.append("******************** Encabezado solicitud " + solicitud.getId() + " ********************");

		pintarDatos(rowData);

		// contarValores(rutaBdFox + bdCabecera);
		log.info(pd.toString());

		return rowData;

	}

	@Override
	public List<Object> insertarComprobantes(List<ComprobanteViaticoEntity> comprobantes,
			SolicitudViaticosEntity solicitud, int numPoliza) {

		// contarValores(rutaBdFox + bdComprobantes);

		pd.append("\n");
		pd.append("******************** Detalle Comprobantes    ********************");
		int c = 1;

		List<Object> prePolizas = new ArrayList<Object>();

		List<Prepoliza> prepoliza = prepoPort.generarPolizaDetalle(comprobantes, solicitud, false);
		for (Prepoliza prepol : prepoliza) {

			pd.append("\n");
			pd.append("[----------- Posicion " + c + " -----------]");

			prePolizas.add(agregarRowDataPrepoliza(prepol));

		}

		return prePolizas;

		// contarValores(rutaBdFox + bdComprobantes);
		// readDBF(rutaBdFox + bdComprobantes);

	}

	public Object agregarRowDataPrepoliza(Prepoliza prepoliza) {

		int valor = 0;

		// Se crea el objeto writer
		// DBFWriter writer = new DBFWriter(new File(rutaBdFox + bdComprobantes));

		// Se agregan los datos
		Object[] rowData = new Object[15];

		try {
			String f = prepoliza.getFecha();
			f = f.substring(8, 10) + "/" + f.substring(5, 7) + "/" + f.substring(0, 4);
			Date date = UtilidadesAdapter.cadenaAFechaConFormato(f, "dd/MM/yyyy");
			String d = UtilidadesAdapter.formatearFechaDDMMYYYY(date);
			rowData[0] = d;// dia mes año
		} catch (ParseException e) {
			e.printStackTrace();
		} // Fecha carga de poliza
		rowData[1] = "3"; // Tipo pol
		rowData[2] = nPoliza;// Consecutivo Poliza

		// Tomar subcuenta

		if (!prepoliza.getSubCuenta().isEmpty()) {
			prepoliza.getSubCuenta().trim();
			String[] datosSC = prepoliza.getSubCuenta().split("-");

			if (datosSC.length != 0) {

				valor = Integer.valueOf(datosSC[0].trim());
				rowData[3] = valor; // parte 1 de subcuenta
				valor = Integer.valueOf(datosSC[1].trim());
				rowData[4] = valor; // parte 2 de subcuenta
				valor = Integer.valueOf(datosSC[2].trim());
				rowData[5] = valor; // parte 3 de subcuenta
				// parte 4 de subcuenta
				if (datosSC.length > 3) {
					valor = Integer.valueOf(datosSC[3]);
					rowData[6] = valor;
				} else {
					rowData[6] = 0;
				}

			}
		}

		rowData[7] = prepoliza.getConcepto();// Concepto
		rowData[8] = prepoliza.getUuid();// uuid
		rowData[9] = prepoliza.getRfc();// rfc
		rowData[10] = BigDecimal.ZERO;// nmonto
		rowData[11] = prepoliza.getCargo();// cargo
		rowData[12] = prepoliza.getAbono();// abono

		String ceco = prepoliza.getCeco();
		int cecoInt = 0;
		if (!ceco.equals("")) {
			cecoInt = Integer.valueOf(ceco.trim());
		}

		rowData[13] = cecoInt;// numcen

		String flujo = prepoliza.getFlujo();
		int flujoInt = 0;
		if (!flujo.equals("")) {
			flujoInt = Integer.valueOf(flujo);
		}

		rowData[14] = flujoInt;// nflujo

		// writer.addRecord(rowData);
		// Se cierra el writer
		// writer.close();
		pintarDatos(rowData);

		return rowData;
	}

	@Override
	public List<Object> insertarNacionales(List<ComprobanteViaticoEntity> comprobantes,
			SolicitudViaticosEntity solicitud, int numPoliza) {

		// contarValores(rutaBdFox + bdNacionales);

		List<Object> nacionales = new ArrayList<Object>();

		for (ComprobanteViaticoEntity comp : comprobantes) {

			// Validar si no tiene xml no agregar posicion
			if (!comp.getRutaXml().equals("")) {

				pd.append("\n");
				pd.append("******************** Nacionales ********************");

				// Se crea el objeto writer
				// DBFWriter writer = new DBFWriter(new File(rutaBdFox + bdNacionales));

				// Se agregan los datos
				Object[] rowData = new Object[14];
				String rfcEmisor = "";
				
				if(comp.getCfdiEntity() != null) {
					
					rfcEmisor = comp.getCfdiEntity().getRfcEmisor();
					
					if(comp.getCfdiEntity().getRfcEmisor().length() < 13) {
						rfcEmisor = StringUtils.leftPad(rfcEmisor, 13, " ");
					}
				}

				rowData[0] = "3"; // Tipo poliza
				rowData[1] = nPoliza; // Consecutivo poliza
				Date d = UtilidadesAdapter.formatearDateaDate(new Date(), "dd/MM/yyyy");
				String date = UtilidadesAdapter.formatearFechaDDMMYYYY(d);
				rowData[2] = date; // Fecha poliza
				rowData[3] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getUuid() : ""; // UUid
				rowData[4] = rfcEmisor;		//comp.getCfdiEntity() != null ? comp.getCfdiEntity().getRfcEmisor() : ""; // rfc emisor
				rowData[5] = comp.getTotal(); // nmonto
				rowData[6] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getFormaPago() : ""; // tipo pago
				rowData[7] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getMoneda() : ""; // Moneda
				rowData[8] = 1; // Ntc

				Date fecha = null;
				try {
					fecha = comp.getCfdiEntity() != null
							? UtilidadesAdapter.formatearDateaDate(comp.getCfdiEntity().getFecha(), "dd/MM/yyyy")
							: UtilidadesAdapter.cadenaAFecha("0000-00-00");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String fechaStr = UtilidadesAdapter.formatearFechaDDMMYYYY(fecha);
				rowData[9] = fechaStr; // Fecha
				rowData[10] = ""; // Cbanco
				rowData[11] = comp.getCfdiEntity() != null
						? UtilidadesAdapter.quitarLetras(comp.getCfdiEntity().getFolio())
						: 0; // Folio
				rowData[12] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getSerie() : ""; // Serie
				
				rowData[13] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getNombreEmisor() : ""; //nombre Emisor
				// writer.addRecord(rowData);
				// writer.close();

				pintarDatos(rowData);

				nacionales.add(rowData);

			}
		}
		pd.append("\n");
		pd.append(
				"----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		pd.append("\n");

		// contarValores(rutaBdFox + bdNacionales);

		log.info(pd.toString());

		return nacionales;
	}

	@Override
	public List<Object> insertarDoctos(List<ComprobanteViaticoEntity> comprobantes, SolicitudViaticosEntity solicitud,
			int numPoliza) {
		// contarValores(rutaBdFox + bdDoctos);

		List<Object> doctos = new ArrayList<Object>();

		for (ComprobanteViaticoEntity comp : comprobantes) {

			// Validar si no tiene xml no agregar posicion
			if (!comp.getRutaXml().equals("")) {

				pd.append("\n");
				pd.append("******************** Doctos ********************");

				// Se crea el objeto writer
				// DBFWriter writer = new DBFWriter(new File(rutaBdFox + bdNacionales));

				// Se agregan los datos
				Object[] rowData = new Object[25];

				rowData[0] = "3"; // Tipo poliza
				rowData[1] = nPoliza; // Consecutivo poliza
				// Date d = UtilidadesAdapter.formatearDateaDate(comp.getFechaCarga(),
				// "dd/MM/yyyy");
				String date = UtilidadesAdapter.obtenerFechaActual();// UtilidadesAdapter.formatearFechaDDMMYYYY(d);
				rowData[2] = date; // Fecha poliza
				rowData[3] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getRfcEmisor() : ""; // RFC
				rowData[4] = "1"; // tipo RFC
				rowData[5] = "1"; // tipo
				rowData[6] = "1"; // tipo iva
				rowData[7] = comp.getSub_cuenta_contable() != null ? comp.getSub_cuenta_contable().getDescripcion()
						: ""; // concepto
				rowData[8] = comp.getCfdiEntity() != null ? "16" : ""; // tasa
				rowData[9] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getSubtotal() : 0; // Subtotal
				rowData[10] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getIva() : 0; // iva
				rowData[11] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getIvaRetenido() : 0; // iva ret
				rowData[12] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getIsrRetenido() : 0; // isr ret
				rowData[13] = "1";// comp.getCfdiEntity() != null ? comp.getCfdiEntity().getIepsRetenido() : "";
									// // ip ret
				rowData[14] = 0; // nagente
				rowData[15] = 0;// "1"; // npedimento
				rowData[16] = ""; // caduana
				rowData[17] = 0; // ntc

				Date fecha = null;
				try {
					fecha = comp.getCfdiEntity() != null
							? UtilidadesAdapter.formatearDateaDate(comp.getCfdiEntity().getFecha(), "dd/MM/yyyy")
							: UtilidadesAdapter.cadenaAFecha("0000-00-00");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String fechaStr = UtilidadesAdapter.formatearFechaDDMMYYYY(fecha);
				rowData[18] = fechaStr; // ffecpag
				rowData[19] = ""; // ccuenta
				rowData[20] = 0; // mmescon
				rowData[21] = 0; // nañocon
				rowData[22] = "1"; // cpago
				rowData[23] = comp.getCfdiEntity() != null && !"".contains(comp.getCfdiEntity().getFolio()) ? comp.getCfdiEntity().getFolio() : 0; // nfolfac
				rowData[24] = comp.getCfdiEntity() != null ? comp.getCfdiEntity().getSerie() : ""; // serie

				// writer.addRecord(rowData);
				// writer.close();

				pintarDatos(rowData);

				doctos.add(rowData);

			}
		}
		pd.append("\n");
		pd.append(
				"----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		pd.append("\n");

		// contarValores(rutaBdFox + bdNacionales);

		log.info(pd.toString());

		return doctos;
	}

	public static String readDBF(String path) {

		StringBuilder sb = new StringBuilder();
		DBFReader reader = null;
		String data = "";

		try {

			// Se crea DBF writer
			reader = new DBFReader(new FileInputStream(path));

			// Se toma cuantos campos son
			int fieldsCount = reader.getFieldCount();

			log.info("numero:" + fieldsCount);
			sb.append("\n");

			// Se toma cuantos campos son
			int recorCount = reader.getRecordCount();

			log.info("numero records:" + recorCount);
			sb.append("\n");

			// Se usa el fieldscount para barrer los nombres de los campos
			int pos = 1;
			StringBuilder tipos = new StringBuilder();
			tipos.append("\n");

			for (int i = 0; i < fieldsCount; i++) {

				DBFField field = reader.getField(i);
				// data = c + "/" + field.getName() + "-" + field.getType() + field.getLength();
				data = field.getName();
				tipos.append("Posición:" + pos + " | Nombre:" + field.getName() + " | Tipo:" + field.getType()
						+ " | Tamaño:" + field.getLength() + "\n");
				pos++;
				sb.append(addspace(field.getLength() - data.length(), data) + "|");
				// c++;
			}
			sb.append("\n");

			log.info(tipos.toString());

			// Para pintar todos los valores
			Object[] rowValues;
			// A strip to take records in the path file

			int tam = 0;
			while ((rowValues = reader.nextRecord()) != null) {
				for (int i = 0; i < rowValues.length; i++) {
					if (i == 0) {
						tam = i + fieldsCount;
					}
					data = identificarTipoValor(rowValues[i]);
					// data = (String) rowValues[i];
					sb.append(addspace(reader.getField(i).getLength() - data.length(), data) + "|");
					if (i + 1 == tam) {
						tam = tam + i;
						sb.append("\n");
					}
				}
			}

			// Para leer los campos, por nombre
//			DBFRow row;
//			while ((row = reader.nextRow()) != null) {
//				log.info(row.getString("CCLAVE"));
//				log.info(row.getString("CNOMBRE"));
//			}

			log.info("Datos---------");
			log.info(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	@Override
	public int obtenerUltimoValorCons() {

		DBFReader reader = null;
		int ultimoValor = 0;

		try {

			String f = UtilidadesAdapter.obtenerFechaActual();
			String mes = f.substring(5, 7);
			String anioReader = "";
			String tipoReader = "";
			int numPol = 0;
			String numPolS = "";

			// Se crea DBF writer
			String ruta = "";
			if ("qas".equals(ambiente)) {
				ruta = rutaBdFoxQAS; // QAS
			} else if ("pro".equals(ambiente)) {
				ruta = rutaBdFoxPRO; // PRO
			}
			reader = new DBFReader(new FileInputStream(ruta + bdCabecera));

			// Para pintar todos los valores
			Object[] rowValues;

			while ((rowValues = reader.nextRecord()) != null) {
				tipoReader = "" + rowValues[0];
				anioReader = "" + rowValues[2];
				log.info("fecha reader: " + anioReader);
				if (anioReader != null) {
					if (!anioReader.equals("null")) {

						anioReader = anioReader.substring(0, 11) + anioReader.substring(24, 28);
						System.out.println(anioReader);

						SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyy", Locale.ENGLISH);
						Date parsedDate = sdf.parse(anioReader);
						SimpleDateFormat print = new SimpleDateFormat("YYYY-MM-dd");
						String ff = print.format(parsedDate);
						// log.info("fecha format:"+ff);
						anioReader = ff.substring(5, 7);
					} else {
						anioReader = "";
					}
				}
				numPolS = rowValues[1] + "";
				numPol = Integer.valueOf(numPolS);
				// System.out.println("mes:"+anioReader+"-"+mes);
				// System.out.println("tipo reader:"+tipoReader);
				if (mes.equals(anioReader)) {
					if (tipoReader.equals("3")) {
						// System.out.println("se tomara y se sumara:"+numPol);
						if (numPol > ultimoValor) {
							ultimoValor = numPol;
						}
					}
				}
			}
			log.info("Ultimo Valor:" + ultimoValor);

		} catch (Exception e) {
			e.printStackTrace();
			reader.close();
		}

		reader.close();
		return ultimoValor;
	}

	public int obtenerValores() {

		DBFReader reader = null;
		String ultimoValor = "";
		int valor = 0;

		try {

			// Se crea DBF writer
			String ruta = "";
			if ("qas".equals(ambiente)) {
				ruta = rutaBdFoxQAS; // QAS
			} else if ("pro".equals(ambiente)) {
				ruta = rutaBdFoxPRO; // PRO
			}
			reader = new DBFReader(new FileInputStream(ruta + bdCabecera));

			// Se toma cuantos campos son
			int fieldsCount = reader.getRecordCount();

			log.info("numero:" + fieldsCount);

			// Para pintar todos los valores
			Object[] rowValues;

			int count = 0;
			while ((rowValues = reader.nextRecord()) != null) {
				count++;
				Date d = (Date) rowValues[2];
				String anio = "";
				anio = UtilidadesAdapter.formatearFecha(d).substring(0, 4);
				anio = rowValues[16] + "";

				if (anio.equals("2021")) {
					log.info("" + UtilidadesAdapter.formatearFecha(d).substring(0, 4));
				}

			}
			log.info("count:" + count);

			if (!ultimoValor.equals("")) {
				Integer.valueOf(ultimoValor);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return valor;
	}

	public int contarValores(String ruta) {

		DBFReader reader = null;
		int recordsCount = 0;

		try {

			// Se crea DBF writer
			reader = new DBFReader(new FileInputStream(ruta));

			// Se toma cuantos campos son
			recordsCount = reader.getRecordCount();

			log.info("total renglones:" + recordsCount);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recordsCount;
	}

	public static void writeDBF(String path) {

		try {

			// Se define el archivo DBFFileds
			DBFField[] fields = new DBFField[2];

			// Definicar cada field con su nombre, tipo y tamaño
			fields[0] = new DBFField();
			fields[0].setName("CCCLAVE");
			fields[0].setType(DBFDataType.CHARACTER);
			fields[0].setLength(10);

			fields[1] = new DBFField();
			fields[1].setName("CNOMBRE");
			fields[1].setType(DBFDataType.CHARACTER);
			fields[1].setLength(20);

			// Se define DBFWriter para escribir en DBF file
			DBFWriter writer = new DBFWriter(new FileOutputStream(path));
			writer.setFields(fields);

			// Escribir los datos de los records
			Object[] rowData = new Object[2];
			rowData[0] = "0300";
			rowData[1] = "John";
			writer.addRecord(rowData);

			rowData = new Object[2];
			rowData[0] = "0500";
			rowData[1] = "Lalit";
			writer.addRecord(rowData);

			// Se cierra el writer
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeExistingDBF(String path) throws DBFException {

		// Se crea el objeto writer
		DBFWriter writer = new DBFWriter(new File(path));

		// Se agregan los datos
		Object[] rowData = new Object[2];
		rowData[0] = "097";
		rowData[1] = "RAul2";
		writer.addRecord(rowData);

		Object[] rowData2 = new Object[2];
		rowData2[0] = "096";
		rowData2[1] = "Riego Ag2";
		writer.addRecord(rowData2);

		// Se cierra el writer
		writer.close();
	}

	private static String addspace(int i, String str) {
		StringBuilder str1 = new StringBuilder();
		for (int j = 0; j < i; j++) {
			str1.append(" ");
		}
		str1.append(str);
		return str1.toString();
	}

	public void pintarDatos(Object[] rowData) {

		pd.append("\n");
		for (Object object : rowData) {
			if (object != null) {
				pd.append(object.toString() + " | ");
			} else {
				pd.append(object + " | ");
			}
		}

	}

	private static String identificarTipoValor(Object o) {
		String s = "";
		if (o == null) {
			return s;
		}
		if (o.getClass() == Integer.class) {
			s = o.toString();
		} else if (o.getClass() == Float.class) {
			s = o.toString();
		} else if (o.getClass() == BigDecimal.class) {
			s = o.toString();
		} else if (o.getClass() == Date.class) {
			Date d = (Date) o;
			s = UtilidadesAdapter.formatearFecha(d);
		} else if (o.getClass() == String.class) {
			s = (String) o;

		}

		return s;
	}

	@Override
	public String leerTabla(String tabla) {
		
		String ruta = "";
		if ("qas".equals(ambiente)) {
			ruta = rutaBdFoxQAS; // QAS
		} else if ("pro".equals(ambiente)) {
			ruta = rutaBdFoxPRO; // PRO
		}
		
		String path = "";
		String salida = "";
		switch (tabla) {
		case "cabecera":
			path = ruta + bdCabecera;
			break;
		case "detalle":
			path = ruta + bdComprobantes;
			break;
		case "nacional":
			path = ruta + bdNacionales;
			break;

		default:
			break;
		}
		if (!"".equals(path)) {
			salida = ContabilizarFoxRepository.readDBF(path);
		}
		return salida;
	}

	public static void main(String args[]) throws DBFException {
		// ContabilizarFoxRepository.writeDBF("C:\\DBF\\ubica.DBF");
		// ContabilizarFoxRepository.writeExistingDBF("C:\\DBF\\ubica.dbf");
		// ContabilizarFoxRepository o = new ContabilizarFoxRepository();
		// o.obtenerUltimoValorCons();
		 ContabilizarFoxRepository.readDBF("C:\\DBF\\poldet.dbf");
		// ContabilizarFoxRepository.readDBF("C:\\Contabilidad\\Datos\\SECOAP21\\poldet.dbf");
		// ContabilizarFoxRepository o = new ContabilizarFoxRepository();
		// o.obtenerUltimoValorCons();
		// o.obtenerValores();
	}

	@Override
	public Object encabezado() {
		Object[] rowData = new Object[3];

		rowData[0] = "3"; // Tipo
		rowData[1] = "123";// Consecutivo
		Date d = UtilidadesAdapter.formatearDateaDate(new Date(), "dd/MM/yyyy");
		rowData[2] = d; // Fecha carga

		return rowData;
	}

}
