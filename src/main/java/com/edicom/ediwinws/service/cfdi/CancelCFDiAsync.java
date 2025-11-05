
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
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rfcE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="rfcR" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="uuid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="pfx" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element name="pfxPassword" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sustitucion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="test" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
    "user",
    "password",
    "rfcE",
    "rfcR",
    "uuid",
    "total",
    "pfx",
    "pfxPassword",
    "motivo",
    "sustitucion",
    "test"
})
@XmlRootElement(name = "cancelCFDiAsync")
public class CancelCFDiAsync {

    @XmlElement(required = true)
    protected String user;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String rfcE;
    @XmlElement(required = true)
    protected String rfcR;
    @XmlElement(required = true)
    protected String uuid;
    protected double total;
    @XmlElement(required = true)
    protected byte[] pfx;
    @XmlElement(required = true)
    protected String pfxPassword;
    @XmlElement(required = true)
    protected String motivo;
    @XmlElement(required = true)
    protected String sustitucion;
    protected boolean test;

    /**
     * Obtiene el valor de la propiedad user.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Define el valor de la propiedad user.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
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
     * Obtiene el valor de la propiedad rfcR.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfcR() {
        return rfcR;
    }

    /**
     * Define el valor de la propiedad rfcR.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfcR(String value) {
        this.rfcR = value;
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

    /**
     * Obtiene el valor de la propiedad total.
     * 
     */
    public double getTotal() {
        return total;
    }

    /**
     * Define el valor de la propiedad total.
     * 
     */
    public void setTotal(double value) {
        this.total = value;
    }

    /**
     * Obtiene el valor de la propiedad pfx.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getPfx() {
        return pfx;
    }

    /**
     * Define el valor de la propiedad pfx.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setPfx(byte[] value) {
        this.pfx = value;
    }

    /**
     * Obtiene el valor de la propiedad pfxPassword.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPfxPassword() {
        return pfxPassword;
    }

    /**
     * Define el valor de la propiedad pfxPassword.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPfxPassword(String value) {
        this.pfxPassword = value;
    }

    /**
     * Obtiene el valor de la propiedad motivo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Define el valor de la propiedad motivo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivo(String value) {
        this.motivo = value;
    }

    /**
     * Obtiene el valor de la propiedad sustitucion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSustitucion() {
        return sustitucion;
    }

    /**
     * Define el valor de la propiedad sustitucion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSustitucion(String value) {
        this.sustitucion = value;
    }

    /**
     * Obtiene el valor de la propiedad test.
     * 
     */
    public boolean isTest() {
        return test;
    }

    /**
     * Define el valor de la propiedad test.
     * 
     */
    public void setTest(boolean value) {
        this.test = value;
    }

}
