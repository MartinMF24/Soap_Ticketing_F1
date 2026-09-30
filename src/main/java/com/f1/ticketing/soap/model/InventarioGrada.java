package com.f1.ticketing.soap.model;

import com.f1.ticketing.soap.adapter.LocalDateAdapter;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "inventario_grada")
@XmlRootElement(name = "InventarioGrada", namespace = "http://f1.ticketing.soap/legacy")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventarioGrada", namespace = "http://f1.ticketing.soap/legacy", propOrder = {
    "id",
    "codigoEvento",
    "carrera",
    "fechaCarrera",
    "tribuna",
    "categoria",
    "precioUsd",
    "stock"
})
public class InventarioGrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "id")
    private Long id;

    @Column(name = "codigo_evento", nullable = false, length = 50)
    @XmlElement(name = "codigoEvento", required = true)
    private String codigoEvento;

    @Column(name = "carrera", nullable = false, length = 100)
    @XmlElement(name = "carrera", required = true)
    private String carrera;

    @Column(name = "fecha_carrera", nullable = false)
    @XmlElement(name = "fechaCarrera", required = true)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fechaCarrera;

    @Column(name = "tribuna", nullable = false, length = 100)
    @XmlElement(name = "tribuna", required = true)
    private String tribuna;

    @Column(name = "categoria", nullable = false, length = 50)
    @XmlElement(name = "categoria", required = true)
    private String categoria;

    @Column(name = "precio_usd", nullable = false, precision = 10, scale = 2)
    @XmlElement(name = "precioUsd", required = true)
    private BigDecimal precioUsd;

    @Column(name = "stock", nullable = false)
    @XmlElement(name = "stock", required = true)
    private Integer stock;

    public InventarioGrada() {
    }

    public InventarioGrada(String codigoEvento, String carrera, LocalDate fechaCarrera,
                           String tribuna, String categoria, BigDecimal precioUsd, Integer stock) {
        this.codigoEvento = codigoEvento;
        this.carrera = carrera;
        this.fechaCarrera = fechaCarrera;
        this.tribuna = tribuna;
        this.categoria = categoria;
        this.precioUsd = precioUsd;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public LocalDate getFechaCarrera() {
        return fechaCarrera;
    }

    public void setFechaCarrera(LocalDate fechaCarrera) {
        this.fechaCarrera = fechaCarrera;
    }

    public String getTribuna() {
        return tribuna;
    }

    public void setTribuna(String tribuna) {
        this.tribuna = tribuna;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioUsd() {
        return precioUsd;
    }

    public void setPrecioUsd(BigDecimal precioUsd) {
        this.precioUsd = precioUsd;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioGrada that = (InventarioGrada) o;
        return Objects.equals(id, that.id) && Objects.equals(codigoEvento, that.codigoEvento) && Objects.equals(tribuna, that.tribuna);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigoEvento, tribuna);
    }

    @Override
    public String toString() {
        return "InventarioGrada{" +
                "id=" + id +
                ", codigoEvento='" + codigoEvento + '\'' +
                ", carrera='" + carrera + '\'' +
                ", fechaCarrera=" + fechaCarrera +
                ", tribuna='" + tribuna + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precioUsd=" + precioUsd +
                ", stock=" + stock +
                '}';
    }
}
