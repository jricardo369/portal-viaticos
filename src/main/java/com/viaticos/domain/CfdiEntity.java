package com.viaticos.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "cfdi")
public class CfdiEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cfdi")
	private int idCfdi;

	@Column(name = "moneda", length = 3)
	private String moneda;

	@Column(name = "fecha")
	@Temporal(TemporalType.DATE)
	private Date fecha;

	@Column(name = "iva", precision = 14, scale = 2)
	private float iva;

	@Column(name = "isr", precision = 14, scale = 2)
	private float isr;

	@Column(name = "ish", precision = 14, scale = 2)
	private float ish;

	@Column(name = "ieps", precision = 14, scale = 2)
	private float ieps;

	@Column(name = "iva_retenido", precision = 14, scale = 2)
	private float ivaRetenido;

	@Column(name = "isr_retenido", precision = 14, scale = 2)
	private float isrRetenido;

	@Column(name = "ieps_retenido", precision = 14, scale = 2)
	private float iepsRetenido;

	@Column(name = "tua", precision = 14, scale = 2)
	private float tua;

	@Column(name = "numerofactura", length = 25)
	private String numeroFactura;

	@Column(name = "subtotal", precision = 14, scale = 2)
	private float subtotal;

	@Column(name = "total", precision = 14, scale = 2)
	private float total;

	@Column(name = "rfc_emisor", length = 13)
	private String rfcEmisor;

	@Column(name = "nombre_emisor", length = 60)
	private String nombreEmisor;

	@Column(name = "rfc_receptor", length = 13)
	private String rfcReceptor;

	@Column(name = "uuid", length = 55)
	private String uuid;

	@Column(name = "folio", length = 10)
	private String folio;

	@Column(name = "serie", length = 30)
	private String serie;

	@Column(name = "forma_pago", length = 30)
	private String formaPago;

	@Column(name = "metodo_pago", length = 30)
	private String metodoPago;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comprobante_viatico", nullable = false)
	private ComprobanteViaticoEntity id_comprobante_viatico;

	public String getNombreEmisor() {
		return nombreEmisor;
	}

	public void setNombreEmisor(String nombreEmisor) {
		this.nombreEmisor = nombreEmisor;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public int getIdCfdi() {
		return idCfdi;
	}

	public void setIdCfdi(int idCfdi) {
		this.idCfdi = idCfdi;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public float getIva() {
		return iva;
	}

	public void setIva(float iva) {
		this.iva = iva;
	}

	public float getIsr() {
		return isr;
	}

	public void setIsr(float isr) {
		this.isr = isr;
	}

	public float getIsh() {
		return ish;
	}

	public void setIsh(float ish) {
		this.ish = ish;
	}

	public float getIeps() {
		return ieps;
	}

	public void setIeps(float ieps) {
		this.ieps = ieps;
	}

	public float getTua() {
		return tua;
	}

	public void setTua(float tua) {
		this.tua = tua;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public String getRfcEmisor() {
		return rfcEmisor;
	}

	public void setRfcEmisor(String rfcEmisor) {
		this.rfcEmisor = rfcEmisor;
	}

	public String getRfcReceptor() {
		return rfcReceptor;
	}

	public void setRfcReceptor(String rfcReceptor) {
		this.rfcReceptor = rfcReceptor;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ComprobanteViaticoEntity getId_comprobante_viatico() {
		return id_comprobante_viatico;
	}

	public void setId_comprobante_viatico(ComprobanteViaticoEntity id_comprobante_viatico) {
		this.id_comprobante_viatico = id_comprobante_viatico;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}

	public float getIvaRetenido() {

		return ivaRetenido;
	}

	public void setIvaRetenido(float ivaRetenido) {
		this.ivaRetenido = ivaRetenido;
	}

	public float getIsrRetenido() {
		return isrRetenido;
	}

	public void setIsrRetenido(float isrRetenido) {
		this.isrRetenido = isrRetenido;
	}

	public float getIepsRetenido() {
		return iepsRetenido;
	}

	public void setIepsRetenido(float iepsRetenido) {
		this.iepsRetenido = iepsRetenido;
	}

	public CfdiEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

}
