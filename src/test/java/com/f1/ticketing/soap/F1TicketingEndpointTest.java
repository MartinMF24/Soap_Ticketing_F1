package com.f1.ticketing.soap;

import com.f1.ticketing.soap.config.WebServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@SpringBootTest
class F1TicketingEndpointTest {

    @Autowired
    private ApplicationContext applicationContext;

    private MockWebServiceClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    @DisplayName("Endpoint SOAP: Debe responder a consultarDisponibilidadRequest con las gradas correspondientes")
    void testSoapConsultarDisponibilidad() {
        Source requestPayload = new StringSource(
                "<consultarDisponibilidadRequest xmlns=\"http://f1.ticketing.soap/legacy\">" +
                "  <codigoEvento>F1-2026-SAO</codigoEvento>" +
                "</consultarDisponibilidadRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(xpath("//leg:gradas", CollectionsMap("leg", "http://f1.ticketing.soap/legacy")).exists())
                .andExpect(xpath("//leg:gradas/leg:carrera", CollectionsMap("leg", "http://f1.ticketing.soap/legacy")).evaluatesTo("São Paulo"));
    }

    @Test
    @DisplayName("Endpoint SOAP: Debe responder a reservarEntradasRequest con un código de confirmación")
    void testSoapReservarEntradasExitoso() {
        Source requestPayload = new StringSource(
                "<reservarEntradasRequest xmlns=\"http://f1.ticketing.soap/legacy\">" +
                "  <codigoEvento>F1-2026-AUS</codigoEvento>" +
                "  <tribuna>Main Grandstand</tribuna>" +
                "  <cantidad>2</cantidad>" +
                "</reservarEntradasRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(xpath("//leg:codigoConfirmacion", CollectionsMap("leg", "http://f1.ticketing.soap/legacy")).exists());
    }

    @Test
    @DisplayName("Endpoint SOAP: Debe retornar SOAP Fault cuando no hay stock suficiente")
    void testSoapReservarEntradasSinStockFault() {
        Source requestPayload = new StringSource(
                "<reservarEntradasRequest xmlns=\"http://f1.ticketing.soap/legacy\">" +
                "  <codigoEvento>F1-2026-AUS</codigoEvento>" +
                "  <tribuna>Main Grandstand</tribuna>" +
                "  <cantidad>999999</cantidad>" +
                "</reservarEntradasRequest>"
        );

        mockClient.sendRequest(withPayload(requestPayload))
                .andExpect(clientOrSenderFault());
    }

    private java.util.Map<String, String> CollectionsMap(String prefix, String uri) {
        return java.util.Map.of(prefix, uri);
    }
}
