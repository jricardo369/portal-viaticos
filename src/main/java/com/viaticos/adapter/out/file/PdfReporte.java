package com.viaticos.adapter.out.file;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.EmpresaPort;
import com.viaticos.application.port.out.EstatusSolicitudPort;
import com.viaticos.application.port.out.UsuariosPort;
import com.viaticos.domain.EmpresaEntity;
import com.viaticos.domain.EstatusSolicitudEntity;
import com.viaticos.domain.Solicitud;
import com.viaticos.domain.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource(ignoreResourceNotFound = true, value = "classpath:configuraciones-viaticos.properties")
public class PdfReporte {

	@Value("${logo}")
	private static String rutaLogo;

	@Autowired
	private CfdiPort cfdiPort;

	@Autowired
	private UsuariosPort usuariosPort;

	@Autowired
	private EmpresaPort empresaPort;
	
	@Autowired
	private EstatusSolicitudPort estPort;

	boolean local = false;

	private static String rutaPdfLocal = "C:/ReportedDeViatico.pdf";

	private static final Font normal = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Light.ttf", 9,
			Font.NORMAL);
	private static final Font negrita = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 9, Font.BOLD);
	private static final Font normalTabla = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Light.ttf", 7,
			Font.NORMAL);
	private static final Font negritaTabla = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 7,
			Font.BOLD);
	private static final Font negritaTitulos = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 7,
			Font.NORMAL, BaseColor.WHITE);
	private static final Font negritaTitulo = FontFactory.getFont("C:\\assets\\font\\roboto\\Roboto-Bold.ttf", 12,
			Font.BOLD);

	public static void main(String args[]) {

		try {
			try {

				List<Solicitud> l = new ArrayList<Solicitud>();
				for (int i = 0; i < 10; i++) {
					Solicitud s = new Solicitud();
					s.setNumeroSolicitud("233");
					s.setEstatus("Pendiente");
					s.setMotivo("Viaje");
					s.setFechaInicio(new Date());
					s.setFechaFin(new Date());
					s.setTotalComprobado(new BigDecimal("2300.00"));
					s.setTotalAnticipo(new BigDecimal("2800.00"));

					Usuario u = new Usuario();
					List<String> emp = new ArrayList<>();
					emp.set(0, "1001");
					// u.setEmpresa(null);
					u.setNombre("Alfonso Medina");
					s.setUsuarioObj(u);
					l.add(s);
				}

				PdfReporte r = new PdfReporte();
				r.generarPdfSolicitud(l, "23435", "Pendiente", "1001", "", "");
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public byte[] generarPdfSolicitud(List<Solicitud> solicitudes, String empleado, String estatus, String empresa,
			String fechaInicio, String fechaFin) throws FileNotFoundException, DocumentException {

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


		byte[] pdfBytes = null;
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Document document = new Document(PageSize.LETTER);
		document.setMargins(12, 12, 30, 20);
		document.setMarginMirroring(true);

		if (local) {
			String nombreArchivo = rutaPdfLocal;
			PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
		} else {
			PdfWriter.getInstance(document, byteStream);
		}
		document.open();
		generaDocumentoGeneral(document, solicitudes, empleado, estatus, empresaListF.toString(), fechaInicio,
				fechaFin);

		document.close();

		pdfBytes = byteStream.toByteArray();
		return pdfBytes;
	}

	public void generaDocumentoGeneral(Document document, List<Solicitud> solicitudes, String empleado, String estatus,
			String empresa, String fechaInicio, String fechaFin) throws FileNotFoundException, DocumentException {
		crearTablaLogo(document,fechaInicio,fechaFin);
		espacio(document, 25f);
		cabeceraSolicitud(document, empleado, empresa, estatus);
		cabeceraSolicitudP2(document, empleado, empresa, estatus);
		espacio(document, 25f);
		titulo(document, "Solicitudes de " + fechaInicio + " a " + fechaFin);
		espacio(document, 5f);

		datosDetalle(document, solicitudes);
	}

	public static void crearTablaLogo(Document document, String fechaInicio, String fechaFin) throws DocumentException {

		PdfPTable tabla = new PdfPTable(2);
		tabla.setWidthPercentage(100);
		tabla.setWidths(new int[] { 7, 16 });
		PdfPCell cell = null;
		try {

			// System.out.println("ruta:"+rutaLogo);
			//Image img = Image.getInstance("C:\\assets\\logo\\SLAPI.png");
			Image img = Image.getInstance("C:\\assets\\logo\\VACIO.png");
			img.setWidthPercentage(50);

			cell = new PdfPCell(img, true);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(0);
			cell.setFixedHeight(50f);
			tabla.addCell(cell);

			tabla.addCell(PdfUtilidad.cell("Reporte de solicitudes "+fechaInicio+" a "+fechaFin+"", negritaTitulo, 0,
					"izquierda", ""));

			document.add(tabla);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public PdfPTable cabeceraSolicitud(Document document, String empleado, String empresa, String estatus)
			throws FileNotFoundException, DocumentException {

		String nombre = "";
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 2, 5, 2, 4,2,4 });

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

		table.addCell(PdfUtilidad.cell("Usuario", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(nombre, normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Fecha reporte", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(new Date()), normal, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Hora reporte", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell(UtilidadesAdapter.horaActual(new Date()), normal, 0, "izquierda", ""));

		document.add(table);

		return table;
	}
	
	public PdfPTable cabeceraSolicitudP2(Document document, String empleado, String empresa, String estatus)
			throws FileNotFoundException, DocumentException {

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 2,9 });

		if (empresa.equals("Todas")) {
			table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));
		} else {
			table.addCell(PdfUtilidad.cell("Empresa", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(empresa, normal, 0, "izquierda", ""));
		}

		if (estatus.equals("Todos")) {
			table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));
		} else {
			table.addCell(PdfUtilidad.cell("Estatus", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(estatus, normal, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
			table.addCell(PdfUtilidad.cell("", normal, 0, "izquierda", ""));
		}

		document.add(table);

		return table;
	}

	public PdfPTable datosDetalle(Document document, List<Solicitud> solicitudes)
			throws FileNotFoundException, DocumentException {

		PdfPTable table = new PdfPTable(9);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 2, 2,2, 1, 1, 1, 1, 1 });

		table.addCell(PdfUtilidad.cell("N\u00famero solicitud", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Estatus", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Empleado", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Motivo", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Fecha creación", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Fecha inicio", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Fecha fin", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Total comprobado", negritaTitulos, 1, "izquierda", "gris"));
		table.addCell(PdfUtilidad.cell("Total anticipo", negritaTitulos, 1, "izquierda", "gris"));

		BigDecimal totalTC = new BigDecimal("0.00");
		BigDecimal totalTA = new BigDecimal("0.00");

		for (Solicitud s : solicitudes) {

			table.addCell(PdfUtilidad.cell(s.getNumeroSolicitud(), normalTabla, 1, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(s.getEstatusDescripcion(), normalTabla, 1, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(s.getNombreCompletoUsuario() != null ? s.getNombreCompletoUsuario() : "",
					normalTabla, 1, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(s.getMotivo() != null ? s.getMotivo() : "", normalTabla, 1, "izquierda", ""));
			table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(s.getFechaCreacion()), normalTabla, 1,"izquierda", ""));
			table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(s.getFechaInicio()), normalTabla, 1,"izquierda", ""));
			table.addCell(PdfUtilidad.cell(UtilidadesAdapter.formatearFecha(s.getFechaFin()), normalTabla, 1,"izquierda", ""));
			BigDecimal totalComp = cfdiPort.totalComprobado(Integer.valueOf(s.getNumeroSolicitud())) != null
					? cfdiPort.totalComprobado(Integer.valueOf(s.getNumeroSolicitud()))
					: BigDecimal.ZERO;
			table.addCell(PdfUtilidad.cell(totalComp, normalTabla, 1, "derecha", ""));
			table.addCell(PdfUtilidad.cell(s.getTotalAnticipo(), normalTabla, 1, "derecha", ""));
			totalTC = totalTC.add(totalComp);
			totalTA = totalTA.add(s.getTotalAnticipo());

		}

		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", normalTabla, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("Totales", negritaTabla, 0, "derecha", ""));
		table.addCell(PdfUtilidad.cell(totalTC, normalTabla, 1, "derecha", ""));
		table.addCell(PdfUtilidad.cell(totalTA, normalTabla, 1, "derecha", ""));

		document.add(table);

		return table;
	}

	public static void titulo(Document document, String titulo) throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 1, 1, 1, });
		table.addCell(PdfUtilidad.cell("Comprobantes", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		table.addCell(PdfUtilidad.cell("", negrita, 0, "izquierda", ""));
		document.add(table);
	}

	public static void espacio(Document document, float altura) throws FileNotFoundException, DocumentException {

		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 20 });

		PdfPCell cell = new PdfPCell();
		cell.setFixedHeight(altura);
		cell.setBorder(0);
		table.addCell(cell);

		document.add(table);
	}

}
