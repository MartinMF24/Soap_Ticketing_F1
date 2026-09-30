package com.f1.ticketing.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codigoEvento"
})
@XmlRootElement(name = "consultarDisponibilidadRequest", namespace = "http://f1.ticketing.soap/legacy")
public class ConsultarDisponibilidadRequest {

    @XmlElement(name = "codigoEvento", namespace = "http://f1.ticketing.soap/legacy", required = true)
    private String codigoEvento;

    public ConsultarDisponibilidadRequest() {
    }

    public ConsultarDisponibilidadRequest(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }
}
