package com.f1.ticketing.soap.exception;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "SinStockFault", namespace = "http://f1.ticketing.soap/legacy")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SinStockFault", namespace = "http://f1.ticketing.soap/legacy", propOrder = {
        "mensaje",
        "codigoError"
})
public class SinStockFault {

    @XmlElement(name = "mensaje", required = true)
    private String mensaje;

    @XmlElement(name = "codigoError", required = true)
    private String codigoError;

    public SinStockFault() {
    }

    public SinStockFault(String mensaje, String codigoError) {
        this.mensaje = mensaje;
        this.codigoError = codigoError;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }
}
