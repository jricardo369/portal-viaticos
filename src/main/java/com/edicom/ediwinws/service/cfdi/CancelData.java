
package com.edicom.ediwinws.service.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CancelData complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CancelData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ack" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cancelQueryData" type="{http://cfdi.service.ediwinws.edicom.com}CancelQueryData"/&gt;
 *         &lt;element name="rfcE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="statusCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancelData", propOrder = {
    "ack",
    "cancelQueryData",
    "rfcE",
    "status",
    "statusCode",
    "uuid"
})
public class CancelData {

    @XmlElement(required = true, nillable = true)
    protected String ack;
    @XmlElement(required = true, nillable = true)
    protected CancelQueryData cancelQueryData;
    @XmlElement(required = true, nillable = true)
    protected String rfcE;
    @XmlElement(required = true, nillable = true)
    protected String status;
    @XmlElement(required = true, nillable = true)
    protected String statusCode;
    @XmlElement(required = true, nillable = true)
    protected String uuid;

    /**
     * Obtiene el valor de la propiedad ack.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAck() {
        return ack;
    }

    /**
     * Define el valor de la propiedad ack.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAck(String value) {
        this.ack = value;
    }

    /**
     * Obtiene el valor de la propiedad cancelQueryData.
     * 
     * @return
     *     possible object is
     *     {@link CancelQueryData }
     *     
     */
    public CancelQueryData getCancelQueryData() {
        return cancelQueryData;
    }

    /**
     * Define el valor de la propiedad cancelQueryData.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelQueryData }
     *     
     */
    public void setCancelQueryData(CancelQueryData value) {
        this.cancelQueryData = value;
    }

    /**
     * Obtiene el valor de la propiedad rfcE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfcE() {
        return rfcE;
    }

    /**
     * Define el valor de la propiedad rfcE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfcE(String value) {
        this.rfcE = value;
    }

    /**
     * Obtiene el valor de la propiedad status.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Define el valor de la propiedad status.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Obtiene el valor de la propiedad statusCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Define el valor de la propiedad statusCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusCode(String value) {
        this.statusCode = value;
    }

    /**
     * Obtiene el valor de la propiedad uuid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Define el valor de la propiedad uuid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
