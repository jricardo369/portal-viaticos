package com.viaticos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.viaticos.domain.EmpresaAprobacionEntity;
import com.viaticos.domain.sql.nu3.OrganizacionesModel;

public class UtilidadesAdapter {

	public static String formatearFecha(Date fecha) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strFecha = "";
		if (fecha != null) {
			strFecha = formatter.format(fecha);
		} else {
			return strFecha;
		}
		return strFecha;
	}

	public static String formatearFechaDDMMYYYY(Date fecha) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strFecha = "";
		if (fecha != null) {
			strFecha = formatter.format(fecha);
		} else {
			return strFecha;
		}
		return strFecha;
	}

	public static String formatearFechaConHora(Date fecha) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strFecha = formatter.format(fecha);
		return strFecha;
	}

	public static String horaActual(Date fecha) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss z");
		String strFecha = formatter.format(fecha);
		return strFecha;
	}

	public static Date cadenaAFecha(String fecha) throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
		return date;
	}

	public static Date cadenaAFechaConFormato(String fecha, String format) throws ParseException {
		Date date = new SimpleDateFormat(format).parse(fecha);
		return date;
	}

	public static String formatNumber(Object number) {
		Locale currentLocale = Locale.getDefault();
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
		return currencyFormatter.format(number);
	}

	public static String tomarAnioActual() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	}

	public static String obtenerFechaActual() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String obtenerHoraActual() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String obtenerFechaYHoraActual() {
		return obtenerFechaActual() + " " + obtenerHoraActual();
	}

	public static Date formatearDateaDate(Date date, String format) {
		String f = UtilidadesAdapter.formatearFecha(date);
		Date d = null;
		;
		try {
			f = f.substring(8, 10) + "/" + f.substring(5, 7) + "/" + f.substring(0, 4);
			d = cadenaAFechaConFormato(f, format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Properties readPropertiesFile(String fileName) throws IOException {
		File f = new File(fileName);
		FileInputStream fis = null;
		Properties prop = null;

		try {
			fis = new FileInputStream(f);
			prop = new Properties();
			prop.load(fis);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			fis.close();
		}

		return prop;
	}

	public static Date sumarDiasAFecha(Date fecha, int dias) {
		if (dias == 0)
			return fecha;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.DAY_OF_YEAR, dias);
		return calendar.getTime();
	}

	public static int quitarLetras(String cadena) {
		String str;
		System.out.println(cadena);
		if (!"".equals(cadena)) {
			str = cadena.replaceAll("[^\\d.]", "");

			return Integer.valueOf(str);
		} else {
			return 0;
		}

	}

	public static String textoLog(String texto) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(UtilidadesAdapter.formatearFechaConHora(new Date()) + " " + texto);
		return sb.toString();
	}
	
	public static boolean esAprobacionAdicional(List<OrganizacionesModel> orgs, List<EmpresaAprobacionEntity> empsAprb) {
		boolean salida = false;
		Set<String> rfcEmpresas = empsAprb.stream().map(EmpresaAprobacionEntity::getEmpresa)
				.collect(Collectors.toSet());

		boolean hayCoincidencia = orgs.stream().anyMatch(org -> rfcEmpresas.contains(org.getRfc()));

		if (hayCoincidencia) {
			System.out.println("✅ Al menos una organización tiene un RFC que está en la lista de empresas.");
			salida = true;
		}else {
			 System.out.println("❌ Ninguna organización comparte RFC con las empresas.");
		}

		return salida;
	}

	public static void main(String args[]) throws ParseException {

		List<OrganizacionesModel> organizaciones = new ArrayList<>();
		OrganizacionesModel a = new OrganizacionesModel();
		a.setId("1");
		a.setNombre("A");
		a.setRfc("RFC001");
		organizaciones.add(a);
		OrganizacionesModel a2 = new OrganizacionesModel();
		a2.setId("2");
		a2.setNombre("B");
		a2.setRfc("RFC002");
		organizaciones.add(a2);
		OrganizacionesModel a3 = new OrganizacionesModel();
		a3.setId("3");
		a3.setNombre("C");
		a3.setRfc("RFC003");
		organizaciones.add(a3);
		
		List<EmpresaAprobacionEntity> emps = new ArrayList<>();
		EmpresaAprobacionEntity e = new EmpresaAprobacionEntity();
		e.setCodigo_empresa("1");
		e.setEmpresa("RFC002");
		emps.add(e);
		EmpresaAprobacionEntity e2 = new EmpresaAprobacionEntity();
		e2.setCodigo_empresa("2");
		e2.setEmpresa("RFC023");
		emps.add(e2);
		EmpresaAprobacionEntity e3 = new EmpresaAprobacionEntity();
		e3.setCodigo_empresa("2");
		e3.setEmpresa("RFC012");
		emps.add(e3);
		
		UtilidadesAdapter.esAprobacionAdicional(organizaciones, emps);
		
	}

}
