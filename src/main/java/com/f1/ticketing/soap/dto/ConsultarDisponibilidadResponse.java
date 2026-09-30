package com.f1.ticketing.soap.dto;

import com.f1.ticketing.soap.model.InventarioGrada;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "gradas"
})
@XmlRootElement(name = "consultarDisponibilidadResponse", namespace = "http://f1.ticketing.soap/legacy")
public class ConsultarDisponibilidadResponse {

    @XmlElement(name = "gradas", namespace = "http://f1.ticketing.soap/legacy")
    private List<InventarioGrada> gradas;

    public ConsultarDisponibilidadResponse() {
        this.gradas = new ArrayList<>();
    }

    public ConsultarDisponibilidadResponse(List<InventarioGrada> gradas) {
        this.gradas = (gradas != null) ? gradas : new ArrayList<>();
    }

    public List<InventarioGrada> getGradas() {
        if (gradas == null) {
            gradas = new ArrayList<>();
        }
        return gradas;
    }

    public void setGradas(List<InventarioGrada> gradas) {
        this.gradas = gradas;
    }
}
