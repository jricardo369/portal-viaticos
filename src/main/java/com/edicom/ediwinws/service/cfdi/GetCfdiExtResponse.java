
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
 *         &lt;element name="getCfdiExtReturn" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
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
    "getCfdiExtReturn"
})
@XmlRootElement(name = "getCfdiExtResponse")
public class GetCfdiExtResponse {

    @XmlElement(required = true)
    protected byte[] getCfdiExtReturn;

    /**
     * Obtiene el valor de la propiedad getCfdiExtReturn.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetCfdiExtReturn() {
        return getCfdiExtReturn;
    }

    /**
     * Define el valor de la propiedad getCfdiExtReturn.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetCfdiExtReturn(byte[] value) {
        this.getCfdiExtReturn = value;
    }

}
