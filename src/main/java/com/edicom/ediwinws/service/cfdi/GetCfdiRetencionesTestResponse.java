
package com.edicom.ediwinws.service.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="getCfdiRetencionesTestReturn" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getCfdiRetencionesTestReturn"
})
@XmlRootElement(name = "getCfdiRetencionesTestResponse")
public class GetCfdiRetencionesTestResponse {

    @XmlElement(required = true)
    protected byte[] getCfdiRetencionesTestReturn;

    /**
     * Obtiene el valor de la propiedad getCfdiRetencionesTestReturn.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetCfdiRetencionesTestReturn() {
        return getCfdiRetencionesTestReturn;
    }

    /**
     * Define el valor de la propiedad getCfdiRetencionesTestReturn.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetCfdiRetencionesTestReturn(byte[] value) {
        this.getCfdiRetencionesTestReturn = value;
    }

}
