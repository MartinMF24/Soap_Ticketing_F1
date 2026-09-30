package com.f1.ticketing.soap;

import com.f1.ticketing.soap.exception.SinStockException;
import com.f1.ticketing.soap.model.InventarioGrada;
import com.f1.ticketing.soap.repository.InventarioGradaRepository;
import com.f1.ticketing.soap.service.F1TicketingSoapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class F1TicketingSoapServiceTest {

    @Autowired
    private F1TicketingSoapService ticketingService;

    @Autowired
    private InventarioGradaRepository repository;

    @Test
    @DisplayName("Debe cargar las 76 gradas de las 19 carreras desde data.sql")
    void testTotalGradasCargadas() {
        long total = repository.count();
        assertEquals(76, total, "Debe haber 76 filas en inventario_grada provenientes de las 19 carreras");
    }

    @Test
    @DisplayName("consultarDisponibilidad: debe retornar las 4 gradas del evento Madrid F1-2026-MAD")
    void testConsultarDisponibilidadMadrid() {
        List<InventarioGrada> gradas = ticketingService.consultarDisponibilidad("F1-2026-MAD");

        assertNotNull(gradas);
        assertEquals(4, gradas.size(), "Madrid debe tener 4 tribunas");

        InventarioGrada paddock = gradas.stream()
                .filter(g -> g.getTribuna().equals("Paddock Club Madrid"))
                .findFirst()
                .orElse(null);

        assertNotNull(paddock);
        assertEquals("VIP", paddock.getCategoria());
        assertEquals(new BigDecimal("3500.00"), paddock.getPrecioUsd());
        assertEquals(500, paddock.getStock());
    }

    @Test
    @DisplayName("consultarDisponibilidad: debe retornar las gradas del evento São Paulo F1-2026-SAO")
    void testConsultarDisponibilidadSaoPaulo() {
        List<InventarioGrada> gradas = ticketingService.consultarDisponibilidad("F1-2026-SAO");

        assertNotNull(gradas);
        assertEquals(4, gradas.size(), "São Paulo debe tener 4 tribunas");
    }

    @Test
    @DisplayName("reservarEntradas: debe restar el stock y retornar código de confirmación alfanumérico TKT-xxxxx")
    void testReservarEntradasExitoso() throws SinStockException {
        String codigoEvento = "F1-2026-BAK";
        String tribuna = "Paddock Club Azerbaijan";
        int cantidad = 10;

        InventarioGrada antes = repository.findByCodigoEventoAndTribuna(codigoEvento, tribuna).orElseThrow();
        int stockInicial = antes.getStock();

        String confirmacion = ticketingService.reservarEntradas(codigoEvento, tribuna, cantidad);

        assertNotNull(confirmacion);
        assertTrue(confirmacion.startsWith("TKT-"), "El código debe iniciar con TKT-");
        assertTrue(confirmacion.length() >= 8, "El código alfanumérico debe tener formato TKT-XXXXX");

        InventarioGrada despues = repository.findByCodigoEventoAndTribuna(codigoEvento, tribuna).orElseThrow();
        assertEquals(stockInicial - cantidad, despues.getStock(), "El stock debe haberse decrementado en la cantidad solicitada");
    }

    @Test
    @DisplayName("reservarEntradas: debe lanzar SinStockException cuando la cantidad supera el stock disponible")
    void testReservarEntradasSinStock() {
        String codigoEvento = "F1-2026-SIN";
        String tribuna = "Singapore Paddock Club";
        int cantidadExcesiva = 999999;

        SinStockException ex = assertThrows(SinStockException.class, () ->
                ticketingService.reservarEntradas(codigoEvento, tribuna, cantidadExcesiva)
        );

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        assertNotNull(ex.getFaultInfo());
        assertEquals("ERR-STOCK-INSUFICIENTE", ex.getFaultInfo().getCodigoError());
    }

    @Test
    @DisplayName("reservarEntradas: debe lanzar SinStockException si la tribuna no existe")
    void testReservarEntradasTribunaNoExiste() {
        assertThrows(SinStockException.class, () ->
                ticketingService.reservarEntradas("F1-2026-MAD", "Tribuna Fantasma", 2)
        );
    }
}
