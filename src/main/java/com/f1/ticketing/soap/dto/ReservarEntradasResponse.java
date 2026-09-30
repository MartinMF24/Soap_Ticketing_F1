package com.f1.ticketing.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codigoConfirmacion"
})
@XmlRootElement(name = "reservarEntradasResponse", namespace = "http://f1.ticketing.soap/legacy")
public class ReservarEntradasResponse {

    @XmlElement(name = "codigoConfirmacion", namespace = "http://f1.ticketing.soap/legacy", required = true)
    private String codigoConfirmacion;

    public ReservarEntradasResponse() {
    }

    public ReservarEntradasResponse(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }

    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }

    public void setCodigoConfirmacion(String codigoConfirmacion) {
        this.codigoConfirmacion = codigoConfirmacion;
    }
}
