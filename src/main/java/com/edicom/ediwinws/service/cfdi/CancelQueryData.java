
package com.edicom.ediwinws.service.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CancelQueryData complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CancelQueryData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cancelStatus" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isCancelable" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="statusCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancelQueryData", propOrder = {
    "cancelStatus",
    "isCancelable",
    "status",
    "statusCode"
})
public class CancelQueryData {

    @XmlElement(required = true, nillable = true)
    protected String cancelStatus;
    @XmlElement(required = true, nillable = true)
    protected String isCancelable;
    @XmlElement(required = true, nillable = true)
    protected String status;
    @XmlElement(required = true, nillable = true)
    protected String statusCode;

    /**
     * Obtiene el valor de la propiedad cancelStatus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCancelStatus() {
        return cancelStatus;
    }

    /**
     * Define el valor de la propiedad cancelStatus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCancelStatus(String value) {
        this.cancelStatus = value;
    }

    /**
     * Obtiene el valor de la propiedad isCancelable.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsCancelable() {
        return isCancelable;
    }

    /**
     * Define el valor de la propiedad isCancelable.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsCancelable(String value) {
        this.isCancelable = value;
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

}
