package com.f1.ticketing.soap.service;

import com.f1.ticketing.soap.exception.SinStockException;
import com.f1.ticketing.soap.model.InventarioGrada;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "F1TicketingSoapService", targetNamespace = "http://f1.ticketing.soap/legacy")
public interface F1TicketingSoapService {

    /**
     * Busca en la base de datos y retorna una lista de todas las gradas disponibles
     * para ese código de evento, incluyendo sus precios y stock actual.
     *
     * @param codigoEvento Código del evento F1 (ej. "F1-2026-SAO")
     * @return Lista de InventarioGrada disponibles
     */
    @WebMethod(operationName = "consultarDisponibilidad")
    @WebResult(name = "grada")
    List<InventarioGrada> consultarDisponibilidad(
            @WebParam(name = "codigoEvento") String codigoEvento
    );

    /**
     * Reserva entradas para una grada específica de un evento.
     *
     * @param codigoEvento Código del evento F1 (ej. "F1-2026-SAO")
     * @param tribuna Nombre de la tribuna (ej. "Grandstand B (Recta)")
     * @param cantidad Cantidad de entradas a reservar
     * @return Código de confirmación alfanumérico (ej. "TKT-99812")
     * @throws SinStockException Si no hay stock suficiente o la grada no existe
     */
    @WebMethod(operationName = "reservarEntradas")
    @WebResult(name = "codigoConfirmacion")
    String reservarEntradas(
            @WebParam(name = "codigoEvento") String codigoEvento,
            @WebParam(name = "tribuna") String tribuna,
            @WebParam(name = "cantidad") int cantidad
    ) throws SinStockException;
}
