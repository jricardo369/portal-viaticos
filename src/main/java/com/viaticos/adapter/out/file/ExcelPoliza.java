package com.viaticos.adapter.out.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
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

import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.domain.Prepoliza;
import com.viaticos.domain.SolicitudViaticosEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(value = "classpath:configuraciones-sap-b1.properties")
public class ExcelPoliza {

	@Value("${sapb1.tip.operacion}")
	private String tipoOperacion;

	@Autowired
	private CfdiPort cfdiPort;

	Workbook wb;
	ByteArrayOutputStream fileOut;
	CellStyle styleNormal;
	CellStyle styleNegritas;
	CellStyle styleTitulo;
	CellStyle styleSumatorias;
	Font font;

	public ExcelPoliza() {

	}

	public ExcelPoliza(String nombreArchivo) {
		try {
			wb = new XSSFWorkbook();
			// File archivo = new File(nombreArchivo + ".xls");
			// System.out.println("ruta de archivo :" +
			// archivo.getAbsolutePath());
			fileOut = new ByteArrayOutputStream();

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
			font.setFontHeightInPoints((short) 10);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(false);
			styleNormal.setFont(font);

			// Estilo normal negritas
			styleNegritas = wb.createCellStyle();
			font = wb.createFont();
			font.setFontHeightInPoints((short) 10);
			font.setFontName("Cambria");
			font.setItalic(false);
			font.setBold(true);
			styleNegritas.setFont(font);

			// Estilo titulo
			styleTitulo = wb.createCellStyle();
			font = null;
			font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
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
			font.setFontHeightInPoints((short) 10);
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

	public void pintarCeldaNegrita(Row row, String valor, int celda, String estilo, boolean esMoneda) {
		CellStyle style = null;
		if (estilo.equals("normal")) {
			style = styleNormal;
		}
		if (estilo.equals("negrita")) {
			style = styleNegritas;
		}

		style.setWrapText(true);
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
				FileOutputStream out = new FileOutputStream(new File("C:/polizas.xlsx"));
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

	public void pintarLineaBlanca(Sheet sheet, ExcelPoliza archivo, Row renglon) {

		// pintar titulos de tabla
		archivo.pintarCelda(renglon, "", 0, true, false);
		archivo.pintarCelda(renglon, "", 1, true, false);
		archivo.pintarCelda(renglon, "", 2, true, false);
		archivo.pintarCelda(renglon, "", 3, true, false);

	}

	public void pintarSolicitud1(Sheet sheet, Row renglon, SolicitudViaticosEntity s) {

		pintarCelda(renglon, "Solicitud: " + s.getId() + " / Estatus: " + s.getEstatus().getDescripcion(), 0, false,
				false);

	}

	public void pintarSolicitud2(Sheet sheet, Row renglon, SolicitudViaticosEntity s) {
		pintarCelda(renglon, "Empresa: " + s.getEmpresaDescr() + " / Monto anticipo: $" + s.getAnticipo(), 0, false,
				false);

	}

	public void pintarTitulosSABB1(Sheet sheet, Row renglon) {

		// pintar titulos de tabla
		pintarCelda(renglon, "Tipo de gasto", 0, true, false);
		pintarCelda(renglon, "Descripción", 1, true, false);
		pintarCelda(renglon, "RFC", 2, true, false);
		pintarCelda(renglon, "UUID", 3, true, false);
		pintarCelda(renglon, "Subcuenta", 4, true, false);
		pintarCelda(renglon, "Proyecto", 5, true, false);
		pintarCelda(renglon, "Cargo", 6, true, false);
		pintarCelda(renglon, "Abono", 7, true, false);
		pintarCelda(renglon, "Tipo de operación", 8, true, false);

		sheet.setColumnWidth(0, 150 * 37);
		sheet.setColumnWidth(1, 170 * 37);
		sheet.setColumnWidth(2, 120 * 37);
		sheet.setColumnWidth(3, 280 * 37);
		sheet.setColumnWidth(4, 100 * 37);
		sheet.setColumnWidth(5, 100 * 37);
		sheet.setColumnWidth(6, 100 * 37);
		sheet.setColumnWidth(7, 100 * 37);
		sheet.setColumnWidth(8, 100 * 37);

	}

	public void pintarTitulos(Sheet sheet, Row renglon) {

		// pintar titulos de tabla
		pintarCelda(renglon, "Pos.", 0, true, false);
		pintarCelda(renglon, "Tipo", 1, true, false);
		pintarCelda(renglon, "Fecha", 2, true, false);
		pintarCelda(renglon, "Tipo Póliza", 3, true, false);
		pintarCelda(renglon, "Subcuenta", 4, true, false);
		pintarCelda(renglon, "Concepto", 5, true, false);
		pintarCelda(renglon, "UUID", 6, true, false);
		pintarCelda(renglon, "RFC", 7, true, false);
		pintarCelda(renglon, "Cargo", 8, true, false);
		pintarCelda(renglon, "Abono", 9, true, false);
		pintarCelda(renglon, "Ceco", 10, true, false);
		pintarCelda(renglon, "Flujo", 11, true, false);

		sheet.setColumnWidth(0, 50 * 37);
		sheet.setColumnWidth(1, 140 * 37);
		sheet.setColumnWidth(2, 80 * 37);
		sheet.setColumnWidth(3, 80 * 37);
		sheet.setColumnWidth(4, 100 * 37);
		sheet.setColumnWidth(5, 150 * 37);
		sheet.setColumnWidth(6, 280 * 37);
		sheet.setColumnWidth(7, 120 * 37);
		sheet.setColumnWidth(8, 100 * 37);
		sheet.setColumnWidth(9, 100 * 37);
		sheet.setColumnWidth(10, 100 * 37);
		sheet.setColumnWidth(11, 100 * 37);

	}

	public void llenarDatosSAPB1(Sheet sheet, List<Prepoliza> prepolizas, SolicitudViaticosEntity solicitud) {
		int contadorReng = 5;
		int count = 1;
		int tamanioPrepol = prepolizas.size();
		String proyecto = solicitud.getProyecto();
		for (Prepoliza p : prepolizas) {
			System.out.println("count:" + count + "/prepol:" + tamanioPrepol);
			if (count == tamanioPrepol) {
				Row renglon = crearRenglon(sheet, contadorReng);
				pintarCelda(renglon, "", 0, false, false);
				pintarCelda(renglon, "", 1, false, false);
				pintarCelda(renglon, "", 2, false, false);
				pintarCelda(renglon, "", 3, false, false);
				pintarCelda(renglon, "", 4, false, false);
				pintarCeldaNegrita(renglon, "TOTALES", 5, "negrita", false);
				pintarCelda(renglon, p.getCargo().toString(), 6, false, false);
				pintarCelda(renglon, p.getAbono().toString(), 7, false, false);
				pintarCelda(renglon, "", 8, false, false);
			} else {
				Row renglon = crearRenglon(sheet, contadorReng);
				pintarCelda(renglon, p.getTipoGasto(), 0, false, false);
				pintarCelda(renglon, p.getConcepto(), 1, false, false);
				pintarCelda(renglon, p.getRfc(), 2, false, false);
				// uuid = "".equals(p.getUuid()) ? "N/A" : p.getUuid();
				pintarCelda(renglon, p.getUuid(), 3, false, false);
				pintarCelda(renglon, p.getSubCuenta(), 4, false, false);
				pintarCelda(renglon, proyecto, 5, false, false);
				pintarCelda(renglon, p.getCargo().toString(), 6, false, false);
				pintarCelda(renglon, p.getAbono().toString(), 7, false, false);
				pintarCelda(renglon, tipoOperacion, 8, false, false);
			}
			contadorReng++;
			count++;
		}

	}

	public void llenarDatos(Sheet sheet, List<Prepoliza> prepolizas, SolicitudViaticosEntity solicitud) {
		int contadorReng = 5;
		for (Prepoliza p : prepolizas) {

			Row renglon = crearRenglon(sheet, contadorReng);
			pintarCelda(renglon, p.getPosicion(), 0, false, false);
			pintarCelda(renglon, p.getTipo(), 1, false, false);
			pintarCelda(renglon, p.getFecha(), 2, false, false);
			pintarCelda(renglon, p.getTipoPoliza(), 3, false, false);
			pintarCelda(renglon, p.getSubCuenta(), 4, false, false);
			pintarCelda(renglon, p.getConcepto(), 5, false, false);
			pintarCelda(renglon, p.getUuid(), 6, false, false);
			pintarCelda(renglon, p.getRfc(), 7, false, false);
			pintarCelda(renglon, p.getCargo().toString(), 8, false, false);
			pintarCelda(renglon, p.getAbono().toString(), 9, false, false);
			pintarCelda(renglon, p.getCeco(), 10, false, false);
			pintarCelda(renglon, p.getFlujo(), 11, false, false);

			contadorReng++;
		}

	}

	public byte[] generarXLS(List<Prepoliza> prepolizas, SolicitudViaticosEntity solicitud, String empresa) {

		crearDatos();

		Sheet sheet = crearHoja("Poliza");

		Row renglon0 = crearRenglon(sheet, 0);
		pintarCelda(renglon0, "Poliza " + " fecha", 0, true, false);
		fusionarCeldas(sheet, 0, 0, 0, 3);

		Row renglon2 = crearRenglon(sheet, 1);
		pintarSolicitud1(sheet, renglon2, solicitud);
		fusionarCeldas(sheet, 1, 1, 0, 4);

		Row renglon3 = crearRenglon(sheet, 2);
		pintarSolicitud2(sheet, renglon3, solicitud);
		fusionarCeldas(sheet, 2, 2, 0, 4);

		BigDecimal totalComp = cfdiPort.totalComprobado(Integer.valueOf(1));
		System.out.println("ttotalComp:" + totalComp);

		if (empresa.equals("SAPB1")) {
			llenarDatosSAPB1(sheet, prepolizas, solicitud);
			Row renglon4 = crearRenglon(sheet, 4);
			pintarTitulosSABB1(sheet, renglon4);
		} else {
			llenarDatos(sheet, prepolizas, solicitud);
			Row renglon4 = crearRenglon(sheet, 4);
			pintarTitulos(sheet, renglon4);
		}

		return generarArchivo(true);

	}

}
