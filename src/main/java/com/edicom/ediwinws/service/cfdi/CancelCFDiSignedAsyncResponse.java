
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
 *         &lt;element name="cancelCFDiSignedAsyncReturn" type="{http://cfdi.service.ediwinws.edicom.com}CancelData"/&gt;
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
    "cancelCFDiSignedAsyncReturn"
})
@XmlRootElement(name = "cancelCFDiSignedAsyncResponse")
public class CancelCFDiSignedAsyncResponse {

    @XmlElement(required = true)
    protected CancelData cancelCFDiSignedAsyncReturn;

    /**
     * Obtiene el valor de la propiedad cancelCFDiSignedAsyncReturn.
     * 
     * @return
     *     possible object is
     *     {@link CancelData }
     *     
     */
    public CancelData getCancelCFDiSignedAsyncReturn() {
        return cancelCFDiSignedAsyncReturn;
    }

    /**
     * Define el valor de la propiedad cancelCFDiSignedAsyncReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelData }
     *     
     */
    public void setCancelCFDiSignedAsyncReturn(CancelData value) {
        this.cancelCFDiSignedAsyncReturn = value;
    }

}
