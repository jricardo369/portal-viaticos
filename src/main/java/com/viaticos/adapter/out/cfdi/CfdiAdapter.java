package com.viaticos.adapter.out.cfdi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.viaticos.UtilidadesAdapter;
import com.viaticos.application.port.out.CfdiPort;
import com.viaticos.application.port.out.jpa.mysql.CfdiJPA;
import com.viaticos.domain.Cfdi;
import com.viaticos.domain.CfdiEntity;
import com.viaticos.domain.Impuesto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class CfdiAdapter implements CfdiPort {

	@Autowired
	private CfdiJPA cfdiJpa;

	@Override
	public Cfdi validarCfdi(byte[] archivo) {
		System.out.println("VALIDAR XML 1.0.4 (" + Charset.defaultCharset() + ")");
		try {

			archivo = removerBom(archivo);

			Document document = parse(archivo);

			Cfdi datosXml = null;
			try {
				datosXml = extraerDatos(document);
			} catch (Exception e) {
				e.printStackTrace();
			}

			removerNodosLibres(document);

			return datosXml;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			Cfdi datosXml = new Cfdi();
			datosXml.setValida(false);
			datosXml.setMensaje(e.getMessage());
			return datosXml;
		}
	}

	@Override
	public boolean existeCfdi(String uuid) {
		// Validar si existe CFDI en base de datos
		return false;
	}

	private byte[] removerBom(byte[] src) {
		if (src[0] != (byte) 0xEF)
			return src;
		if (src[1] != (byte) 0xBB)
			return src;
		if (src[2] != (byte) 0xBF)
			return src;
		byte[] dst = new byte[src.length - 3];
		System.arraycopy(src, 3, dst, 0, dst.length);
		// System.out.println(new String(dst));
		return dst;
	}

	private void removerNodosLibres(Document document) {
		removerAdenda(document);
		removerComplemento(document);
		removerComplementoConcepto(document);
		removerInformacionAduanera(document);
	}

	/**
	 * Extracción de datos del XML
	 * 
	 * @param document
	 * @return un objeto con los datos más importantes del CFDI
	 * @throws ParseException
	 */
	static Cfdi extraerDatos(Document document) throws ParseException {

		String version = getCfdiVersion(document);

		Cfdi d = new Cfdi();
		d.setValida(true);
		d.setVersion(version);

		Element root = document.getDocumentElement();
		NamedNodeMap attributes = root.getAttributes();
		int n = attributes.getLength();
		for (int i = 0; i < n; i++) {
			Node item = attributes.item(i);
			String text = item.getTextContent();
			String name = item.getNodeName();
			if(text != null) {
				text = text.replace(" ","");
				text = text.replaceAll("\\s","");
				text = text.trim();
			}
			switch (name.trim().toLowerCase()) {
			case "moneda":
				d.setMoneda(text);
				break;
			case "serie":
				d.setSerie(text);
				break;
			case "folio":
				d.setFolio(text);
				break;
			case "fecha":
				d.setFecha(UtilidadesAdapter.cadenaAFecha(text));
				break;
			case "total":
				d.setTotal(new BigDecimal(text));
				break;
			case "subtotal":
				d.setSubtotal(new BigDecimal(text));
				break;
			case "nocertificado":
				d.setNoCertificado(text);
				break;
			case "sello":
				d.setSello(text);
				break;
			case "certificado":
				d.setCertificado(text);
				break;
			case "tipodecomprobante":
				d.setTipoComp(text);
				break;
			case "descuento":
				d.setDescuento(new BigDecimal(text));
				break;
			case "metodopago":
				d.setMetodoPago(text);
				break;
			case "formapago":
				d.setFormaPago(text);
				break;

			default:
				break;
			}
		}

		Node emisor = getFirstNamedChild(root, "cfdi:Emisor");
		d.setRfcEmisor(getAttributeOrNullCaseInsensitive(emisor, "rfc"));

		Node receptor = getFirstNamedChild(root, "cfdi:Receptor");
		d.setRfcReceptor(getAttributeOrNullCaseInsensitive(receptor, "rfc"));

		Node impuestos = getFirstNamedChild(root, "cfdi:Impuestos");
		if (impuestos != null) {

			Node traslados = getFirstNamedChild(impuestos, "cfdi:Traslados");
			if (traslados != null) {

				BigDecimal totalImpuestosTrasladados = getBigDecimalAttributeOrNullCaseInsensitive(traslados,
						"totalImpuestosTrasladados");

				BigDecimal totalImpuestosTrasladadosFallback = BigDecimal.ZERO;
				BigDecimal ivaTrasladado = BigDecimal.ZERO;
				BigDecimal iepsTrasladado = BigDecimal.ZERO;

				List<Impuesto> impuestosCfdi = new ArrayList<>();
				for (Node traslado : getNamedChildren(traslados, "cfdi:Traslado")) {

					String impuesto = getAttributeOrNullCaseInsensitive(traslado, "impuesto");
					System.out.println("------impuesto trasladado:" + impuesto);
					BigDecimal importe = getBigDecimalAttributeOrNullCaseInsensitive(traslado, "importe");
					System.out.println("------importe trasladado:" + importe);
					BigDecimal tasaOCuota = getBigDecimalAttributeOrNullCaseInsensitive(traslado, "tasaOCuota");
					System.out.println("------tasaCuota tasaCuota:" + tasaOCuota);
					String tasaS = tasaOCuota.multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN).toString();
					System.out.println("------Tasa al 100:" + tasaS);

					if (impuesto.equals("002")) {
						if (tasaS.equals("0")) {
							d.setTieneIVA0(true);
						}
						if (tasaS.equals("16")) {
							d.setIva16(importe);
							d.setTasaIva16(tasaOCuota);
						}
					}
					Impuesto im = new Impuesto(impuesto, tasaOCuota, importe);
					impuestosCfdi.add(im);

					if (importe != null) //
						totalImpuestosTrasladadosFallback = totalImpuestosTrasladadosFallback.add(importe);
					if (isIeps(impuesto))
						iepsTrasladado = iepsTrasladado.add(importe);
					if (isIva(impuesto)) {
						ivaTrasladado = ivaTrasladado.add(importe);
						d.setTasaiva(tasaOCuota != null ? tasaOCuota.doubleValue() : 0);
					}

				}

				if (totalImpuestosTrasladados == null)
					totalImpuestosTrasladados = totalImpuestosTrasladadosFallback;

				d.setTotalImpuestosTrasladados(new BigDecimal(totalImpuestosTrasladados.toString()));
				d.setIvaTrasladado(new BigDecimal(ivaTrasladado.toString()));
				d.setIepsTrasladado(new BigDecimal(iepsTrasladado.toString()));

				d.setImpuestos(impuestosCfdi);

			}

			Node retenciones = getFirstNamedChild(impuestos, "cfdi:Retenciones");
			if (retenciones != null) {

				BigDecimal totalImpuestosRetenidos = getBigDecimalAttributeOrNullCaseInsensitive(retenciones,
						"totalImpuestosRetenidos");

				BigDecimal nodoTotalImpuestosRetenidosFallback = BigDecimal.ZERO;
				BigDecimal isrRetenido = BigDecimal.ZERO;
				BigDecimal ivaRetenido = BigDecimal.ZERO;
				BigDecimal iepsRetenido = BigDecimal.ZERO;

				for (Node retencion : getNamedChildren(retenciones, "cfdi:Retencion")) {

					String impuesto = getAttributeOrNullCaseInsensitive(retencion, "impuesto");
					BigDecimal importe = getBigDecimalAttributeOrNullCaseInsensitive(retencion, "importe");

					if (importe != null) //
						nodoTotalImpuestosRetenidosFallback = nodoTotalImpuestosRetenidosFallback.add(importe);
					if (isIeps(impuesto))
						iepsRetenido = iepsRetenido.add(importe);
					if (isIva(impuesto))
						ivaRetenido = ivaRetenido.add(importe);
					if (isIsr(impuesto))
						isrRetenido = isrRetenido.add(importe);

				}

				if (totalImpuestosRetenidos == null)
					totalImpuestosRetenidos = nodoTotalImpuestosRetenidosFallback;

				d.setTotalImpuestosRetenidos(new BigDecimal(totalImpuestosRetenidos.toString()));
				d.setIvaRetenido(new BigDecimal(ivaRetenido.toString()));
				d.setIsrRetenido(new BigDecimal(isrRetenido.toString()));
				d.setIepsRetenido(new BigDecimal(iepsRetenido.toString()));
			}
		}

		Node complemento = getFirstNamedChild(root, "cfdi:Complemento");
		if (complemento != null) {
			Node timbreFiscalDigital = getFirstNamedChild(complemento, "tfd:TimbreFiscalDigital");
			d.setUuid(getAttributeOrNullCaseInsensitive(timbreFiscalDigital, "uuid"));
			Node impLocales = getFirstNamedChild(complemento, "implocal:ImpuestosLocales");
			if (impLocales != null) {
				for (Node traslado : getNamedChildren(impLocales, "implocal:TrasladosLocales")) {
					BigDecimal importe = getBigDecimalAttributeOrNullCaseInsensitive(traslado, "Importe");
					d.setIshTrasladado(importe);
				}
			}

		}

		return d;
	}

	/**
	 * Extrae la versión del cfdi del documento proporcionado
	 * 
	 * @param document
	 * @return versión
	 */
	static String getCfdiVersion(Document document) {
		Node versionNode = document.getDocumentElement().getAttributes().getNamedItem("version");
		if (versionNode == null)
			versionNode = document.getDocumentElement().getAttributes().getNamedItem("Version");
		String version = versionNode.getTextContent();
		if (version == null)
			System.err.println("sin versi�n");
		return version;
	}

	private static boolean isIsr(String impuesto) {
		impuesto = impuesto.trim().toUpperCase();
		switch (impuesto) {
		case "ISR":
		case "001":
			return true;
		default:
			return false;
		}
	}

	private static boolean isIva(String impuesto) {
		impuesto = impuesto.trim().toUpperCase();
		switch (impuesto) {
		case "IVA":
		case "002":
			return true;
		default:
			return false;
		}
	}

	private static boolean isIeps(String impuesto) {
		impuesto = impuesto.trim().toUpperCase();
		switch (impuesto) {
		case "IEPS":
		case "003":
			return true;
		default:
			return false;
		}
	}

	private static BigDecimal getBigDecimalAttributeOrNullCaseInsensitive(Node node, String attribute) {
		String text = getAttributeOrNullCaseInsensitive(node, attribute);
		if (text == null)
			return null;
		try {
			return new BigDecimal(text);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	static String getAttributeOrNullCaseInsensitive(Node node, String attribute) {
		NamedNodeMap attributes = node.getAttributes();
		int n = attributes.getLength();
		for (int i = 0; i < n; i++) {
			Node item = attributes.item(i);
			if (item.getNodeName().equalsIgnoreCase(attribute))
				return item.getTextContent();
		}
		return null;
	}

	private static Node getFirstNamedChild(Node element, String tagName) {
		List<Node> list = getNamedChildren(element, tagName);
		return !list.isEmpty() ? list.get(0) : null;
	}

	private static List<Node> getNamedChildren(Node element, String tagName) {
		List<Node> list = new ArrayList<>();
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			if (item.getNodeName().equals(tagName))
				list.add(item);
		}
		return list;
	}

	/**
	 * La adenda no nos interesa para la validaci�n o extracci�n de datos
	 * 
	 * @param document
	 */
	static void removerAdenda(Document document) {
		Node documentElement = document.getDocumentElement();
		List<Node> namedChildren = getNamedChildren(documentElement, "cfdi:Addenda");
		for (int i = namedChildren.size() - 1; i >= 0; i--) {
			Node child = namedChildren.get(i);
			documentElement.removeChild(child);
		}
		Node documentElement2 = document.getDocumentElement();
		List<Node> namedChildren2 = getNamedChildren(documentElement, "Addenda");
		for (int i = namedChildren2.size() - 1; i >= 0; i--) {
			Node child2 = namedChildren2.get(i);
			documentElement2.removeChild(child2);
		}
	}

	/**
	 * Fuera del UUID, no nos interesa el complemento para validar
	 * 
	 * @param document
	 */
	static void removerComplemento(Document document) {
		Node documentElement = document.getDocumentElement();
		List<Node> namedChildren = getNamedChildren(documentElement, "cfdi:Complemento");
		for (int i = namedChildren.size() - 1; i >= 0; i--) {
			Node child = namedChildren.get(i);
			documentElement.removeChild(child);
		}
	}

	/**
	 * El complemento concepto no nos interesa para la validaci�n o extracci�n de
	 * datos
	 * 
	 * @param document
	 */
	static void removerComplementoConcepto(Document document) {
		Node documentElement = document.getDocumentElement();
		for (Node conceptos : getNamedChildren(documentElement, "cfdi:Conceptos")) {
			for (Node concepto : getNamedChildren(conceptos, "cfdi:Concepto")) {
				List<Node> complementoConceptoNodes = getNamedChildren(concepto, "cfdi:ComplementoConcepto");
				for (int i = complementoConceptoNodes.size() - 1; i >= 0; i--) {
					Node complementoConcepto = complementoConceptoNodes.get(i);
					complementoConcepto.getParentNode().removeChild(complementoConcepto);
				}
			}
		}
	}

	/**
	 * �Y por que quitamos informaci�n aduanera? Al momento de codificar este metodo
	 * hab�a una contradicci�n en las restricciones para el nodo, con la expreci�n
	 * regular <code>[0-9]{2} [0-9]{2} [0-9]{4} [0-9]{7}</code> tenia que completar
	 * 21 caract�res, ni m�s ni menos, y pues no se pueden las dos cosas (en la
	 * documentaci�n dice que cada bloque de n�meros se separa por dos espacios,
	 * pero la expresi�n regular dice otra cosa).
	 * 
	 * @param document
	 */
	static void removerInformacionAduanera(Document document) {
		Node documentElement = document.getDocumentElement();
		for (Node conceptos : getNamedChildren(documentElement, "cfdi:Conceptos")) {
			for (Node concepto : getNamedChildren(conceptos, "cfdi:Concepto")) {
				List<Node> informacionAduaneraNodes = getNamedChildren(concepto, "cfdi:InformacionAduanera");
				for (int i = informacionAduaneraNodes.size() - 1; i >= 0; i--) {
					Node informacionAduanera = informacionAduaneraNodes.get(i);
					informacionAduanera.getParentNode().removeChild(informacionAduanera);
				}
			}
		}
	}

	public static void getAllNodesWithNamedAttribute(Set<Node> nodes, Node node, String name) {
		if (node.hasAttributes()) {
			if (node.getAttributes().getNamedItem(name) != null)
				nodes.add(node);
		}
		if (node.hasChildNodes()) {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++)
				getAllNodesWithNamedAttribute(nodes, childNodes.item(i), name);
		}
	}

	public static Node[] getAllOcurrencesInNode(String nodeName, Node node) {
		int count = 0;
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node item = node.getChildNodes().item(i);
			if (item.getNodeName().equals(nodeName))
				count++;
		}
		int index = 0;
		Node[] nodes = new Node[count];
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node item = node.getChildNodes().item(i);
			if (item.getNodeName().equals(nodeName))
				nodes[index++] = item;
		}
		return nodes;
	}

	public static Document parse(byte[] xmlBytes) throws IOException, ParserConfigurationException, SAXException {
		System.out.println("1.0.1");
		DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		DocumentBuilder parser = parserFactory.newDocumentBuilder();
		String text = new String(xmlBytes, "utf-8");
		xmlBytes = text.getBytes("utf-8");
		return parser.parse(new ByteArrayInputStream(xmlBytes));
	}

	@Override
	public boolean existeCfdiJpa(String uuid) {

		CfdiEntity cfdiEn = new CfdiEntity();

		cfdiEn = cfdiJpa.findByUuid(uuid);

		if (cfdiEn == null) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public CfdiEntity obtenerCfdiPorUuid(String uuid) {

		return cfdiJpa.findByUuid(uuid);

	}

	@Override
	public void guardaCfdi(CfdiEntity cfdi) {
		cfdiJpa.save(cfdi);

	}

	public static String tipoPago(String tipoPago) {
		String tipoPagoDesc = "";
		switch (tipoPago) {
		case "01":
			tipoPagoDesc = "Efectivo";
			break;
		case "02":
			tipoPagoDesc = "Cheque nominativo";
			break;
		case "03":
			tipoPagoDesc = "Transferencia electrónica de fondos";
			break;
		case "04":
			tipoPagoDesc = "Tarjeta de crédito";
			break;
		case "05":
			tipoPagoDesc = "Monedero electrónico";
			break;
		case "06":
			tipoPagoDesc = "Dinero electrónico";
			break;
		case "08":
			tipoPagoDesc = "Vales de despensa";
			break;
		case "12":
			tipoPagoDesc = "Dación en pago";
			break;
		case "13":
			tipoPagoDesc = "Pago por subrogación";
			break;
		case "14":
			tipoPagoDesc = "Pago por consignación";
			break;
		case "15":
			tipoPagoDesc = "Condonación";
			break;
		case "17":
			tipoPagoDesc = "Compensación";
			break;
		case "23":
			tipoPagoDesc = "Novación";
			break;
		case "24":
			tipoPagoDesc = "Confusión";
			break;
		case "25":
			tipoPagoDesc = "Remisión de deuda";
			break;
		case "26":
			tipoPagoDesc = "Prescripción o caducidad";
			break;
		case "27":
			tipoPagoDesc = "A satisfacción del acreedor";
			break;
		case "28":
			tipoPagoDesc = "Tarjeta de débito";
			break;
		case "29":
			tipoPagoDesc = "Tarjeta de servicios";
			break;
		case "30":
			tipoPagoDesc = "Aplicación de anticipos";
			break;
		case "99":
			tipoPagoDesc = "Por definir";
			break;
		default:
			break;
		}
		return tipoPagoDesc;
	}

	public static String metodoPago(String metodoPago) {
		String mp = "";
		switch (metodoPago) {
		case "PUE":
			mp = "Pago en una sola exhibición";
			break;
		case "PPD":
			mp = "Pago en parcialidades o diferido";
			break;
		default:
			break;
		}
		return mp;
	}

	@Override
	public BigDecimal totalPorTipoSubCuenta(String codigo, int numeroSolicitud) {
		BigDecimal totalPorTipoSubCuenta = cfdiJpa.totalPorTipoSubCuenta(codigo, numeroSolicitud);
		if (totalPorTipoSubCuenta == null)
			return BigDecimal.ZERO;
		return totalPorTipoSubCuenta;
	}

	@Override
	public BigDecimal totalReintegroPorSolicitud(int numeroSolicitud) {
		BigDecimal totalReintegro = cfdiJpa.totalReintegroSolicitud(numeroSolicitud);
		if (totalReintegro == null)
			return BigDecimal.ZERO;
		return totalReintegro;
	}

	@Override
	public BigDecimal totalPorNombre(String nombre, int numeroSolicitud) {
		BigDecimal totalPorTipoSubCuenta = cfdiJpa.totalPorNombre(nombre, numeroSolicitud);
		if (totalPorTipoSubCuenta == null)
			return BigDecimal.ZERO;
		return totalPorTipoSubCuenta;
	}

	@Override
	public BigDecimal totalPorTipoSubCuentaYFecha(String codigo, int numeroSolicitud, Date fecha) {
		BigDecimal totalPorTipoSubCuenta = cfdiJpa.totalPorTipoSubCuentaYFecha(codigo, numeroSolicitud, fecha);
		if (totalPorTipoSubCuenta == null)
			return BigDecimal.ZERO;
		return totalPorTipoSubCuenta;
	}

	@Override
	public BigDecimal totalPorNombreSubCuentaYFecha(String nombre, int numeroSolicitud, Date fecha) {
		BigDecimal totalPorTipoSubCuenta = cfdiJpa.totalPorTipoSubCuentaYFecha(nombre, numeroSolicitud, fecha);
		if (totalPorTipoSubCuenta == null)
			return BigDecimal.ZERO;
		return totalPorTipoSubCuenta;
	}

	@Override
	public BigDecimal totalPorTipoSubCuentaNotIn(String codigo, int numeroSolicitud) {
		return cfdiJpa.totalPorTipoSubCuentaNotIn(codigo, numeroSolicitud);
	}

	@Override
	public BigDecimal totalPropina(int numeroSolicitud) {
		BigDecimal totalPropina = cfdiJpa.totalPropina(numeroSolicitud);
		if (totalPropina == null)
			return BigDecimal.ZERO;
		return totalPropina;
	}

	@Override
	public BigDecimal totalComprobado(int numeroSolicitud) {
		BigDecimal totalComprobado = cfdiJpa.totalComprobado(numeroSolicitud);
		if (totalComprobado == null)
			return BigDecimal.ZERO;
		return totalComprobado;
	}

	@Override
	public BigDecimal totalComprobadoSinPropina(int numeroSolicitud) {
		BigDecimal totalComprobado = cfdiJpa.totalComprobadoSinPropina(numeroSolicitud);
		if (totalComprobado == null)
			return BigDecimal.ZERO;
		return totalComprobado;
	}

	@Override
	public List<String> totalesDeFecha(Date fecha, int numeroSolicitud) {
		return cfdiJpa.totalesDeFecha(fecha, numeroSolicitud);
	}

	@Override
	public BigDecimal totalNoDeduciblePorAnio(String ejercicio, String usuario) {
		BigDecimal totalNoDeducible = BigDecimal.ZERO;
		BigDecimal totalNDeducible = cfdiJpa.totalTotalNoDeduciblePorAnio(ejercicio, usuario);
		if (totalNDeducible == null) {
			totalNDeducible = BigDecimal.ZERO;
		}
		totalNoDeducible = totalNoDeducible.add(totalNDeducible);
		BigDecimal totalPrpinaNDeducible = cfdiJpa.totalTotalPropinaNoDeduciblePorAnio(ejercicio, usuario);
		if (totalPrpinaNDeducible == null) {
			totalPrpinaNDeducible = BigDecimal.ZERO;
		}
		totalNoDeducible = totalNoDeducible.add(totalPrpinaNDeducible);
		if (totalNoDeducible == null)
			return BigDecimal.ZERO;
		return totalNoDeducible;
	}

	@Override
	public BigDecimal totalNoAplica(int numeroSolicitud, boolean aplica) {
		String aplicaS = "0";
		if (!aplica) {
			aplicaS = "1";
		}
		BigDecimal totalNoDeducible = cfdiJpa.totalNoAplica(numeroSolicitud, aplicaS);
		if (totalNoDeducible == null)
			return BigDecimal.ZERO;
		return totalNoDeducible;
	}

	@Override
	public BigDecimal totalNoAplicaPorComprobante(int numeroSolicitud, boolean aplica, int idComprobante) {
		String aplicaS = "0";
		if (!aplica) {
			aplicaS = "1";
		}
		BigDecimal totalNoDeducible = cfdiJpa.totalNoAplicaPorComprobante(numeroSolicitud, aplicaS, idComprobante);
		if (totalNoDeducible == null)
			return BigDecimal.ZERO;
		return totalNoDeducible;
	}

	@Override
	public BigDecimal totalComprobadoNoDeducible(int numeroSolicitud) {
		BigDecimal totalNoDeducible = cfdiJpa.totalComprobadoNoDeducible(numeroSolicitud);
		if (totalNoDeducible == null)
			return BigDecimal.ZERO;
		return totalNoDeducible;
	}

	@Override
	public void actualizarISH(int idCfdi, BigDecimal ish) {
		cfdiJpa.actulizaISHCfdi(idCfdi, ish);
	}

	@Override
	public BigDecimal totalComprobadoDeducible(int numeroSolicitud) {
		BigDecimal totalNoDeducible = cfdiJpa.totalComprobadoDeducible(numeroSolicitud);
		if (totalNoDeducible == null)
			return BigDecimal.ZERO;
		return totalNoDeducible;
	}

	public static void main(String args[]) {
		File file = new File("C:/xmls/XML Fact. Hotel (2da noche).xml");
		try {

			FileInputStream is = new FileInputStream(file);
			byte archivo[] = new byte[(int) file.length()];
			is.read(archivo);
			CfdiAdapter c = new CfdiAdapter();
			Cfdi cfdi = c.validarCfdi(archivo);
			System.out.println("Total:" + cfdi.getTotal());
			System.out.println("SubTotal:" + cfdi.getSubtotal());
			System.out.println("Total:" + cfdi.getTotal());
			System.out.println("ISH:" + cfdi.getIshTrasladado());
			if (cfdi.getIshTrasladado() != null) {
				System.out.println("no es ISH null");
			}
			System.out.println("UUID:" + cfdi.getUuid());
			System.out.println("Descuento:" + cfdi.getDescuento());
			System.out.println("ISH:" + cfdi.getIshTrasladado());
			System.out.println("Impuestos retenidos:" + cfdi.getTotalImpuestosRetenidos());

			System.out.println("----------------");

			BigDecimal sbR = cfdi.getTotal().subtract(cfdi.getSubtotal()).setScale(2, RoundingMode.DOWN);
			BigDecimal impR = new BigDecimal("299.11").setScale(2, RoundingMode.DOWN);
			BigDecimal total = cfdi.getTotal();
			BigDecimal subtotal = cfdi.getSubtotal();
			System.out.println("Resta total - subtotal, cortado a dos decimales:" + sbR);
			System.out.println("Impuestos cortado a dos decimales:" + impR);
			BigDecimal diferencia = sbR.subtract(impR).abs();
			System.out.println("Diferencia:" + diferencia);
			System.out.println("Uno:" + BigDecimal.ONE);

			boolean mostrarErrorImportes = false;

			// Validar primero impuestos que tiene el ish en el subtotal
			if (cfdi != null && total != null && subtotal != null && (sbR).compareTo(impR) != 0) {
				mostrarErrorImportes = true;
			}

			// Validar tolerancia de 1 arriba o abajo
			if (diferencia.compareTo(BigDecimal.ONE) == 1) {
				System.out.println("Tiene diferencia");
			} else {
				System.out.println("No se tiene diferencia");
			}

			// Validación de impuestos retenidos
			if (mostrarErrorImportes) {

				if (cfdi.getTotalImpuestosRetenidos() == null) {
					System.out.println("No se encontro nodo impuestos retenidos");
					cfdi.setTotalImpuestosRetenidos(BigDecimal.ZERO);

				}

				impR = impR.subtract(cfdi.getTotalImpuestosRetenidos());

				System.out.println("*--------------* VALIDACION CON RETENCIONES");
				System.out.println("imp R:" + impR);
				System.out.println("*--------------*");
				System.out.println("Restara el IMP RET. de impuestos retenidos para validacion");
				System.out.println("Resta total - subtotal - impret, cortado a dos decimales:" + sbR);
				System.out.println("Impuestos cortado a dos decimales:" + impR);

				if (cfdi != null && cfdi.getTotal() != null && cfdi.getSubtotal() != null
						&& (sbR).compareTo(impR) != 0) {
					mostrarErrorImportes = true;
				} else {
					mostrarErrorImportes = false;
				}

			}

			// Validar primero impuestos que tiene el ish en el subtotal
			if (cfdi != null && total != null && subtotal != null && (sbR).compareTo(impR) != 0) {
				mostrarErrorImportes = true;
			} else {
				mostrarErrorImportes = false;
			}

			// Validación de impuesto ISH
			if (mostrarErrorImportes) {

				if (cfdi.getIshTrasladado() == null) {
					System.out.println("No se encontro ISH trasladado");
					cfdi.setIshTrasladado(BigDecimal.ZERO);

				}

				BigDecimal ish = cfdi.getIshTrasladado();
				if (ish == null) {
					ish = BigDecimal.ZERO;
				}
				impR = impR.subtract(ish);

				System.out.println("*--------------* VALIDACION CON ISH TRASLADADO");
				System.out.println("imp R:" + impR);
				System.out.println("*---*");
				System.out.println("Restara el ISH de impuestos para validación");
				System.out.println("Resta total - subtotal - ish, ortado a dos decimales:" + sbR);
				System.out.println("Impuestos cortado a dos decimales:" + impR);
				if (cfdi != null && total != null && subtotal != null && (sbR).compareTo(impR) != 0) {
					mostrarErrorImportes = true;
				} else {
					mostrarErrorImportes = false;
				}

			}

			if (mostrarErrorImportes) {
				System.out.print("Tiene error");
			} else {
				System.out.print("Paso validaciones de impuestos");
			}

			System.out.println("\n\n----------------------------------------------");
			System.out.println("Tiene iva0:" + cfdi.isTieneIVA0());
			System.out.println("Tiene iva16:" + cfdi.getIva16());

			BigDecimal calculo = BigDecimal.ZERO;
			BigDecimal calculoIVA16 = BigDecimal.ZERO;
			boolean tieneIva0 = false;
			BigDecimal iva16 = new BigDecimal("0.00");
			BigDecimal tasaIva16 = new BigDecimal("0.00");

			if (cfdi.getIva16() != null) {
				iva16 = iva16.add(cfdi.getIva16());
			}

			if (cfdi.getTasaIva16() != null) {
				tasaIva16 = tasaIva16.add(cfdi.getTasaIva16());
			}

			calculoIVA16 = BigDecimal.ZERO;

			if (tieneIva0) {
				calculo = cfdi.getSubtotal().subtract(calculoIVA16);
				System.out.print("Tiene iva0:" + calculo);
			} else {
				System.out.println("No tiene iva0");
			}

			if (cfdi.isTieneIVA0()) {
				System.out.println("Si tiene iva 0");
				System.out.println(cfdi.getIva16());
				System.out.println(cfdi.getTasaIva16());
				if (cfdi.getIva16() != null) {
					calculoIVA16 = calculoIVA16.add(cfdi.getIva16());
					calculoIVA16 = calculoIVA16.divide(cfdi.getTasaIva16());
					System.out.println("calculo16:" + calculoIVA16.toString());
				}else {
					System.out.println("Tiene iva 0 pero no tiene iva 16");
				}
			}

			is.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
