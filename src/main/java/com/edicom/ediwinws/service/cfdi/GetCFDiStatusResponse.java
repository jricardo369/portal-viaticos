
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
 *         &lt;element name="getCFDiStatusReturn" type="{http://cfdi.service.ediwinws.edicom.com}CancelQueryData"/&gt;
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
    "getCFDiStatusReturn"
})
@XmlRootElement(name = "getCFDiStatusResponse")
public class GetCFDiStatusResponse {

    @XmlElement(required = true)
    protected CancelQueryData getCFDiStatusReturn;

    /**
     * Obtiene el valor de la propiedad getCFDiStatusReturn.
     * 
     * @return
     *     possible object is
     *     {@link CancelQueryData }
     *     
     */
    public CancelQueryData getGetCFDiStatusReturn() {
        return getCFDiStatusReturn;
    }

    /**
     * Define el valor de la propiedad getCFDiStatusReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelQueryData }
     *     
     */
    public void setGetCFDiStatusReturn(CancelQueryData value) {
        this.getCFDiStatusReturn = value;
    }

}
