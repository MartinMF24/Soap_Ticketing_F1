package com.f1.ticketing.soap.service.impl;

import com.f1.ticketing.soap.exception.SinStockException;
import com.f1.ticketing.soap.exception.SinStockFault;
import com.f1.ticketing.soap.model.InventarioGrada;
import com.f1.ticketing.soap.repository.InventarioGradaRepository;
import com.f1.ticketing.soap.service.F1TicketingSoapService;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@WebService(
        serviceName = "F1TicketingSoapService",
        portName = "F1TicketingSoapPort",
        targetNamespace = "http://f1.ticketing.soap/legacy",
        endpointInterface = "com.f1.ticketing.soap.service.F1TicketingSoapService"
)
public class F1TicketingSoapServiceImpl implements F1TicketingSoapService {

    private static final Logger log = LoggerFactory.getLogger(F1TicketingSoapServiceImpl.class);

    private final InventarioGradaRepository inventarioGradaRepository;

    public F1TicketingSoapServiceImpl(InventarioGradaRepository inventarioGradaRepository) {
        this.inventarioGradaRepository = inventarioGradaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioGrada> consultarDisponibilidad(String codigoEvento) {
        log.info("Consultando disponibilidad de gradas para el evento: {}", codigoEvento);
        if (codigoEvento == null || codigoEvento.isBlank()) {
            return List.of();
        }
        return inventarioGradaRepository.findByCodigoEvento(codigoEvento.trim());
    }

    @Override
    @Transactional
    public String reservarEntradas(String codigoEvento, String tribuna, int cantidad) throws SinStockException {
        log.info("Intento de reserva para evento: {}, tribuna: {}, cantidad: {}", codigoEvento, tribuna, cantidad);

        if (cantidad <= 0) {
            String errorMsg = "La cantidad solicitada debe ser mayor a 0 (recibido: " + cantidad + ").";
            log.warn(errorMsg);
            throw new SinStockException(errorMsg, new SinStockFault(errorMsg, "ERR-CANTIDAD-INVALIDA"));
        }

        if (codigoEvento == null || codigoEvento.isBlank() || tribuna == null || tribuna.isBlank()) {
            String errorMsg = "El código de evento y el nombre de la tribuna no pueden estar vacíos.";
            log.warn(errorMsg);
            throw new SinStockException(errorMsg, new SinStockFault(errorMsg, "ERR-DATOS-INCOMPLETOS"));
        }

        // Buscar la grada específica
        InventarioGrada grada = inventarioGradaRepository
                .findByCodigoEventoAndTribunaIgnoreCase(codigoEvento.trim(), tribuna.trim())
                .orElseThrow(() -> {
                    String errorMsg = String.format("No se encontró la tribuna '%s' para el evento '%s'.",
                            tribuna.trim(), codigoEvento.trim());
                    log.warn(errorMsg);
                    return new SinStockException(errorMsg, new SinStockFault(errorMsg, "ERR-TRIBUNA-NO-ENCONTRADA"));
                });

        // Verificar si stock >= cantidad
        if (grada.getStock() < cantidad) {
            String errorMsg = String.format(
                    "Stock insuficiente para la tribuna '%s' en el evento '%s'. Stock disponible: %d, cantidad solicitada: %d.",
                    grada.getTribuna(), grada.getCodigoEvento(), grada.getStock(), cantidad
            );
            log.warn(errorMsg);
            throw new SinStockException(errorMsg, new SinStockFault(errorMsg, "ERR-STOCK-INSUFICIENTE"));
        }

        // Restar la cantidad y actualizar la base de datos
        grada.setStock(grada.getStock() - cantidad);
        inventarioGradaRepository.save(grada);

        // Generar código alfanumérico de confirmación (ej. "TKT-99812")
        int randomSuffix = ThreadLocalRandom.current().nextInt(10000, 100000);
        String codigoConfirmacion = "TKT-" + randomSuffix;

        log.info("Reserva exitosa. Grada: {}, nuevo stock: {}, código de confirmación: {}",
                grada.getTribuna(), grada.getStock(), codigoConfirmacion);

        return codigoConfirmacion;
    }
}
