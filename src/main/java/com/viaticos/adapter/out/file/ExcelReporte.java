package com.viaticos.adapter.out.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelReporte {

	@Autowired
	private CfdiPort cfdiPort;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EmpresaPort empresaPort;

	@Autowired
	private EstatusSolicitudPort estPort;

	Workbook wb;
	ByteArrayOutputStream fileOut;
	CellStyle styleNormal;
	CellStyle styleNegritas;
	CellStyle styleTitulo;
	CellStyle styleSumatorias;
	Font font;

	public ExcelReporte() {

	}

	public ExcelReporte(String nombreArchivo) {
		try {
			wb = new XSSFWorkbook();
			// File archivo = new File(nombreArchivo + ".xls");
			// System.out.println("ruta de archivo :" +
			// archivo.getAbsolutePath());
			fileOut = new ByteArrayOutputStream();
			// declarar estilos

			// Estilo normal
			styleNormal = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(false);
			styleNormal.setFont(font);

			// Estilo normal negritas
			styleNegritas = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			styleNegritas.setFont(font);

			// Estilo titulo
			styleTitulo = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			font.setColor(IndexedColors.WHITE.getIndex());
			styleTitulo.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			styleTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleTitulo.setFont(font);

			// Estilo sumatorias
			styleSumatorias = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			styleSumatorias.setFont(font);

		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}

	public void crearDatos() {
		try {
			wb = new XSSFWorkbook();
			// File archivo = new File(nombreArchivo + ".xls");
			// System.out.println("ruta de archivo :" +
			// archivo.getAbsolutePath());
			fileOut = new ByteArrayOutputStream();
			// declarar estilos

			// Estilo normal
			styleNormal = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(false);
			styleNormal.setFont(font);

			// Estilo normal negritas
			styleNegritas = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			styleNegritas.setFont(font);

			// Estilo titulo
			styleTitulo = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			font.setColor(IndexedColors.WHITE.getIndex());
			styleTitulo.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			styleTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleTitulo.setFont(font);

			// Estilo sumatorias
			styleSumatorias = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			styleSumatorias.setFont(font);

		} catch (Exception e) {
			System.out.println("Error " + e);
		}
	}

	public Sheet crearHoja(String nombre) {
		Sheet sheet = wb.createSheet(nombre);
		return sheet;
	}

	public Row crearRenglon(Sheet sheet, int renglon) {
		Row row = sheet.createRow((short) renglon);
		return row;
	}

	public void pintarCelda(Row row, String valor, int celda, boolean tipoTitulo, boolean esMoneda) {
		CellStyle style;
		if (!tipoTitulo) {
			style = styleNormal;
		} else {
			style = styleTitulo;
		}
//		if(esMoneda) {
//			style.setDataFormat((short)8);
//		}

		style.setWrapText(true);
		Cell cell = row.createCell(celda);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
	}

	public void pintarCeldaNegritas(Row row, String valor, int celda, boolean tipoTitulo) {
		CellStyle style = styleNegritas;
		Cell cell = row.createCell(celda);
		cell.setCellValue(valor);
		cell.setCellStyle(style);
	}

	public void pintarCeldaNumero(Row row, String valor, int celda, boolean styleTitulo) {
		CellStyle style;
		if (!styleTitulo) {
			style = styleNormal;
		} else {
			style = styleSumatorias;
		}

		Cell cell = row.createCell(celda);
		try {
			cell.setCellValue(Double.parseDouble(valor));
		} catch (NumberFormatException e) {
			cell.setCellValue("");
		}
		style.setDataFormat((short) 7);
		cell.setCellStyle(style);
	}

	public void fusionarCeldas(Sheet sheet, int primerRenglon, int ultimoRenglon, int primerCelda, int ultimaCelda) {
		sheet.addMergedRegion(new CellRangeAddress(primerRenglon, // first row
																	// (0-based)
				ultimoRenglon, // last row (0-based)
				primerCelda, // first column (0-based)
				ultimaCelda // last column (0-based)
		));
	}

	public byte[] generarArchivo(boolean noLocal) {
		try {
			if (noLocal) {
				// escribir y cerrar el archivo
				wb.write(fileOut);
				fileOut.close();
			} else {
				// Local archivo
				FileOutputStream out = new FileOutputStream(new File("c:/reqs.xlsx"));
				wb.write(out);
				out.close();
			}
			System.out.println("Se generó el archivo de xls");
			return fileOut.toByteArray();

		} catch (Exception e) {
			System.out.println("Error no se pudo generar el archivo " + e);
			return null;
		}
	}

	public void pintarLineaBlanca(Sheet sheet, ExcelReporte archivo, Row renglon) {
		// pintar titulos de tabla
		archivo.pintarCelda(renglon, "", 0, true, false);
		archivo.pintarCelda(renglon, "", 1, true, false);
		archivo.pintarCelda(renglon, "", 2, true, false);
		archivo.pintarCelda(renglon, "", 3, true, false);

	}
	
	public void pintarDatosEmpresa(Sheet sheet, Row renglon, String empresa) {
		List<String> empresaListF = new ArrayList<>();
		List<String> emp = new ArrayList<String>(Arrays.asList(empresa.split(",")));
		for (String string : emp) {
			// System.out.println("emp:" + string);
			List<EmpresaEntity> s = empresaPort.obtenerEmpresasPorEmpresa(string);
			if (!s.isEmpty()) {
				for (EmpresaEntity e : s) {
					//System.out.println("Empre:" + e.getEmpresa());
					if (!empresaListF.contains(e.getEmpresa())) {
						empresaListF.add(e.getEmpresa());
						// System.out.println("Lo agrego -----------------" );
					}
				}
			}
		}
		
		
		pintarCelda(renglon, "Empresa: " +  empresaListF.toString(), 0, false, false);
	}

	public void pintarDatosUsuario(Sheet sheet, Row renglon, String empleado) {
		String nombre = "";
		// Obtener datos de usuario
		Usuario user = new Usuario();
		// Busca en tabla Tempus Accesos
		user = usuariosPort.encontrarUsuarioTempusAccesos(empleado);

		if (user.getUsuario() == null) {
			// Busca en tabla Tempus nu3
			user = usuariosPort.encontrarUsuarioIdJPA(empleado);
		}

		if (user.getNombre() != null) {
			nombre = user.getNombre();
		}
		pintarCelda(renglon, "Usuario: " + nombre, 0, false, false);
	}

	public void pintarDatosEstatus(Sheet sheet, Row renglon, String estatus) {
		List<Integer> estatusList = new ArrayList<Integer>();
		if (!estatus.equals("")) {
			String[] estatusArr = estatus.split(",");

			for (String e : estatusArr) {
				estatusList.add(Integer.valueOf(e));
			}
		}
		
		List<EstatusSolicitudEntity> el = estPort.obtenerEstatusList(estatusList);
		StringBuilder estatusStringFinal = new StringBuilder();
		if(!el.isEmpty()) {
			for (EstatusSolicitudEntity est : el) {
				estatusStringFinal = estatusStringFinal.append(est.getDescripcion() + " , ");
			}
		}
		if(estatusStringFinal.length() != 0) {
			estatus = estatusStringFinal.substring(0,estatusStringFinal.length()-2);
		}
		pintarCelda(renglon, "Estatus: " + estatus, 0, false, false);
	}

	public void pintarTitulos(Sheet sheet, Row renglon) {
		// pintar titulos de tabla
		pintarCelda(renglon, "Núm. Sol", 0, true, false);
		pintarCelda(renglon, "Estatus", 1, true, false);
		pintarCelda(renglon, "Empleado", 2, true, false);
		pintarCelda(renglon, "Motivo", 3, true, false);
		pintarCelda(renglon, "Fecha creación", 4, true, false);
		pintarCelda(renglon, "Fecha inicio", 5, true, false);
		pintarCelda(renglon, "Fecha fin", 6, true, false);
		pintarCelda(renglon, "Total comprobado", 7, true, false);
		pintarCelda(renglon, "Total anticipo", 8, true, false);

		sheet.setColumnWidth(0, 60 * 37);
		sheet.setColumnWidth(1, 210 * 37);
		sheet.setColumnWidth(2, 210 * 37);
		sheet.setColumnWidth(3, 220 * 37);
		sheet.setColumnWidth(4, 80 * 37);
		sheet.setColumnWidth(5, 80 * 37);
		sheet.setColumnWidth(6, 80 * 37);
		sheet.setColumnWidth(7, 80 * 37);
		sheet.setColumnWidth(8, 70 * 37);

	}

	public void pintarDatos(Sheet sheet, List<Solicitud> reqs) {

		llenarDatos(sheet, reqs);
	}

	public void llenarDatos(Sheet sheet, List<Solicitud> sols) {
		BigDecimal totalTC = new BigDecimal("0.00");
		BigDecimal totalTA = new BigDecimal("0.00");
		int contadorReng = 6;
		for (Solicitud s : sols) {
			Row renglon = crearRenglon(sheet, contadorReng);
			pintarCelda(renglon, s.getNumeroSolicitud(), 0, false, false);
			pintarCelda(renglon, s.getEstatusDescripcion(), 1, false, false);
			pintarCelda(renglon, s.getNombreCompletoUsuario(), 2, false, false);
			pintarCelda(renglon, s.getMotivo(), 3, false, false);
			pintarCelda(renglon, UtilidadesAdapter.formatearFecha(s.getFechaCreacion()), 4, false, false);
			pintarCelda(renglon, UtilidadesAdapter.formatearFecha(s.getFechaCreacion()), 5, false, false);
			pintarCelda(renglon, UtilidadesAdapter.formatearFecha(s.getFechaCreacion()), 6, false, false);
			BigDecimal totalComp = cfdiPort.totalComprobado(Integer.valueOf(s.getNumeroSolicitud())) != null
					? cfdiPort.totalComprobado(Integer.valueOf(s.getNumeroSolicitud()))
					: BigDecimal.ZERO;
			pintarCelda(renglon, totalComp.toString(), 7, false, false);
			pintarCelda(renglon, s.getTotalAnticipo().toString(), 8, false, false);
			totalTC = totalTC.add(totalComp);
			totalTA = totalTA.add(s.getTotalAnticipo());
			contadorReng++;
		}
		
		Row renglon = crearRenglon(sheet, contadorReng);
		pintarCelda(renglon, "", 0, false, false);
		pintarCelda(renglon, "", 1, false, false);
		pintarCelda(renglon, "", 2, false, false);
		pintarCelda(renglon,"", 3, false, false);
		pintarCelda(renglon, "", 4, false, false);
		pintarCelda(renglon, "", 5, false, false);
		pintarCeldaNegritas(renglon, "Totales", 6, false);
		pintarCelda(renglon, totalTC.toString(), 7, false, false);
		pintarCelda(renglon, totalTA.toString(), 8, false, false);

	}

	public byte[] generarXLS(List<Solicitud> solicitudes, String empleado, String estatus, String empresa,
			String fechaInicio, String fechaFin) {

		crearDatos();

		// ExcelReporte crearExcel = new ExcelReporte("Reporte de solicitudes " +
		// fechaInicio + " a " + fechaFin + "");
		Sheet sheet = crearHoja("Solicitudes");

		Row renglon0 = crearRenglon(sheet, 0);
		pintarCelda(renglon0, "Reporte de solicitudes " + fechaInicio + " a " + fechaFin + "", 0, true, false);
		fusionarCeldas(sheet, 0, 0, 0, 3);
		

		Row renglon2 = crearRenglon(sheet, 1);
		pintarDatosUsuario(sheet, renglon2, empleado);
		fusionarCeldas(sheet, 1, 1, 0, 15);

		Row renglon3 = crearRenglon(sheet, 2);
		pintarDatosEmpresa(sheet, renglon3, empresa);
		fusionarCeldas(sheet, 2, 2, 0, 100);

		Row renglon4 = crearRenglon(sheet, 3);
		pintarDatosEstatus(sheet, renglon4, estatus);
		fusionarCeldas(sheet, 3, 3, 0, 30);

		BigDecimal totalComp = cfdiPort.totalComprobado(Integer.valueOf(1));
		System.out.println("ttotalComp:" + totalComp);

		pintarDatos(sheet, solicitudes);
		Row renglon5 = crearRenglon(sheet, 5);
		pintarTitulos(sheet, renglon5);

		return generarArchivo(true);

	}

}
