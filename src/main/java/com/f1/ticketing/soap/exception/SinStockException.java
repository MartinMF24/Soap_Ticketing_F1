package com.f1.ticketing.soap.exception;

import jakarta.xml.ws.WebFault;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@WebFault(name = "SinStockFault", targetNamespace = "http://f1.ticketing.soap/legacy", faultBean = "com.f1.ticketing.soap.exception.SinStockFault")
@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "Stock insuficiente para realizar la reserva")
public class SinStockException extends Exception {

    private static final long serialVersionUID = 1L;

    private final SinStockFault faultInfo;

    public SinStockException(String message) {
        super(message);
        this.faultInfo = new SinStockFault(message, "ERR-SIN-STOCK");
    }

    public SinStockException(String message, SinStockFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public SinStockException(String message, SinStockFault faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public SinStockFault getFaultInfo() {
        return faultInfo;
    }
}
