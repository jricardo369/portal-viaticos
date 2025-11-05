
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
 *         &lt;element name="getTimbreCfdiRetencionesReturn" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
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
    "getTimbreCfdiRetencionesReturn"
})
@XmlRootElement(name = "getTimbreCfdiRetencionesResponse")
public class GetTimbreCfdiRetencionesResponse {

    @XmlElement(required = true)
    protected byte[] getTimbreCfdiRetencionesReturn;

    /**
     * Obtiene el valor de la propiedad getTimbreCfdiRetencionesReturn.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetTimbreCfdiRetencionesReturn() {
        return getTimbreCfdiRetencionesReturn;
    }

    /**
     * Define el valor de la propiedad getTimbreCfdiRetencionesReturn.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetTimbreCfdiRetencionesReturn(byte[] value) {
        this.getTimbreCfdiRetencionesReturn = value;
    }

}
