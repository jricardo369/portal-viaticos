
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
 *         &lt;element name="cancelCFDiAsyncReturn" type="{http://cfdi.service.ediwinws.edicom.com}CancelData"/&gt;
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
    "cancelCFDiAsyncReturn"
})
@XmlRootElement(name = "cancelCFDiAsyncResponse")
public class CancelCFDiAsyncResponse {

    @XmlElement(required = true)
    protected CancelData cancelCFDiAsyncReturn;

    /**
     * Obtiene el valor de la propiedad cancelCFDiAsyncReturn.
     * 
     * @return
     *     possible object is
     *     {@link CancelData }
     *     
     */
    public CancelData getCancelCFDiAsyncReturn() {
        return cancelCFDiAsyncReturn;
    }

    /**
     * Define el valor de la propiedad cancelCFDiAsyncReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelData }
     *     
     */
    public void setCancelCFDiAsyncReturn(CancelData value) {
        this.cancelCFDiAsyncReturn = value;
    }

}
