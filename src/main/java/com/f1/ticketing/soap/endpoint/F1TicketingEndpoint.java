package com.f1.ticketing.soap.endpoint;

import com.f1.ticketing.soap.dto.ConsultarDisponibilidadRequest;
import com.f1.ticketing.soap.dto.ConsultarDisponibilidadResponse;
import com.f1.ticketing.soap.dto.ReservarEntradasRequest;
import com.f1.ticketing.soap.dto.ReservarEntradasResponse;
import com.f1.ticketing.soap.exception.SinStockException;
import com.f1.ticketing.soap.model.InventarioGrada;
import com.f1.ticketing.soap.service.F1TicketingSoapService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class F1TicketingEndpoint {

    public static final String NAMESPACE_URI = "http://f1.ticketing.soap/legacy";

    private final F1TicketingSoapService ticketingSoapService;

    public F1TicketingEndpoint(F1TicketingSoapService ticketingSoapService) {
        this.ticketingSoapService = ticketingSoapService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consultarDisponibilidadRequest")
    @ResponsePayload
    public ConsultarDisponibilidadResponse consultarDisponibilidad(
            @RequestPayload ConsultarDisponibilidadRequest request) {
        String codigoEvento = request.getCodigoEvento();
        List<InventarioGrada> gradas = ticketingSoapService.consultarDisponibilidad(codigoEvento);
        return new ConsultarDisponibilidadResponse(gradas);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "reservarEntradasRequest")
    @ResponsePayload
    public ReservarEntradasResponse reservarEntradas(
            @RequestPayload ReservarEntradasRequest request) throws SinStockException {
        String confirmacion = ticketingSoapService.reservarEntradas(
                request.getCodigoEvento(),
                request.getTribuna(),
                request.getCantidad()
        );
        return new ReservarEntradasResponse(confirmacion);
    }
}
