package com.f1.ticketing.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codigoEvento",
    "tribuna",
    "cantidad"
})
@XmlRootElement(name = "reservarEntradasRequest", namespace = "http://f1.ticketing.soap/legacy")
public class ReservarEntradasRequest {

    @XmlElement(name = "codigoEvento", namespace = "http://f1.ticketing.soap/legacy", required = true)
    private String codigoEvento;

    @XmlElement(name = "tribuna", namespace = "http://f1.ticketing.soap/legacy", required = true)
    private String tribuna;

    @XmlElement(name = "cantidad", namespace = "http://f1.ticketing.soap/legacy", required = true)
    private int cantidad;

    public ReservarEntradasRequest() {
    }

    public ReservarEntradasRequest(String codigoEvento, String tribuna, int cantidad) {
        this.codigoEvento = codigoEvento;
        this.tribuna = tribuna;
        this.cantidad = cantidad;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getTribuna() {
        return tribuna;
    }

    public void setTribuna(String tribuna) {
        this.tribuna = tribuna;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
