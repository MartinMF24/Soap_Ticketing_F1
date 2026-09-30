# F1 Ticketing Legacy SOAP Service (`f1-ticketing-legacy-soap`)

Microservicio legado independiente desarrollado en **Java 17 / Spring Boot**, simulando ser el proveedor oficial de boletería de la **Fórmula 1**, comunicándose estrictamente mediante el protocolo **XML/SOAP 1.1**.

> 📖 **Para la guía paso a paso y manual de integración completo, consulta el documento:** [**`DOCUMENTACION_SISTEMA_LEGACY.md`**](file:///c:/Users/Martin/Documents/GitHub/Soap_Ticketing_F1/DOCUMENTACION_SISTEMA_LEGACY.md).

---

## 1. Stack Tecnológico y Arquitectura

* **Lenguaje:** Java 17 o superior (verificado con JDK 24).
* **Framework:** Spring Boot 3.3.4.
* **Componentes Principales:**
  * `spring-boot-starter-web-services` (Spring-WS 4.0).
  * `spring-boot-starter-data-jpa` (Hibernate 6.5).
  * `h2` (Base de datos relacional en memoria).
  * `wsdl4j` (Generación dinámica de contratos WSDL 1.1).
  * `jakarta.jws-api` & `jakarta.xml.ws-api` (Anotaciones estándar Jakarta EE: `@WebService`, `@WebMethod`, `@WebFault`).

---

## 2. Modelo de Datos JPA (`InventarioGrada`)

Representa el inventario y stock de cada tribuna por carrera:

| Campo | Tipo | Restricciones / Mapeo | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | PK, `@GeneratedValue(strategy = IDENTITY)` | Identificador único autoincremental |
| `codigoEvento` | `String` | `@Column(name = "codigo_evento")` | Código único del Gran Premio (ej. `F1-2026-SAO`) |
| `carrera` | `String` | `@Column(name = "carrera")` | Ciudad / País sede del evento (ej. `São Paulo`, `Madrid`) |
| `fechaCarrera`| `LocalDate` | `@Column(name = "fecha_carrera")` | Fecha oficial de la carrera (ISO YYYY-MM-DD) |
| `tribuna` | `String` | `@Column(name = "tribuna")` | Nombre oficial de la tribuna (ej. `VIP Club Interlagos`) |
| `categoria` | `String` | `@Column(name = "categoria")` | Categoría de la entrada (`VIP`, `Premium`, `Standard`, `General`) |
| `precioUsd` | `BigDecimal` | `@Column(name = "precio_usd", precision = 10, scale = 2)` | Precio oficial en USD |
| `stock` | `Integer` | `@Column(name = "stock")` | Cantidad de boletos disponibles en inventario |

---

## 3. Carga Inicial de Datos (`data.sql`)

El sistema puebla automáticamente la base de datos H2 en memoria al iniciar mediante `src/main/resources/data.sql`, conteniendo las **76 tribunas correspondientes a los 19 Grandes Premios** del archivo oficial `F1 Ticketeria.xlsx`:

1. **Madrid** (`F1-2026-MAD`) - 2026-09-11
2. **Bakú** (`F1-2026-BAK`) - 2026-09-25
3. **Singapur** (`F1-2026-SIN`) - 2026-10-09
4. **Austin** (`F1-2026-AUS`) - 2026-10-23
5. **Ciudad de México** (`F1-2026-MEX`) - 2026-10-30
6. **São Paulo** (`F1-2026-SAO`) - 2026-11-06
7. **Las Vegas** (`F1-2026-LAS`) - 2026-11-19
8. **Lusail** (`F1-2026-QAT`) - 2026-11-27
9. **Abu Dabi** (`F1-2026-ABU`) - 2026-12-04
10. **Bahréin** (`F1-2027-BAH`) - 2027-03-14
11. **Arabia Saudita** (`F1-2027-SAU`) - 2027-03-21
12. **Australia** (`F1-2027-MEL`) - 2027-04-04
13. **Japón** (`F1-2027-SUZ`) - 2027-04-11
14. **China** (`F1-2027-SHA`) - 2027-04-18
15. **Miami** (`F1-2027-MIA`) - 2027-05-02
16. **Canadá** (`F1-2027-MON`) - 2027-05-23
17. **Mónaco** (`F1-2027-MCO`) - 2027-06-06
18. **Portugal** (`F1-2027-POR`) - 2027-06-20
19. **Gran Bretaña** (`F1-2027-SIL`) - 2027-07-04

---

## 4. Contrato y Operaciones SOAP

El servicio `@WebService` `F1TicketingSoapService` expone dos operaciones principales (`@WebMethod`):

### A. `consultarDisponibilidad(String codigoEvento)`
* **Propósito:** Consulta todas las gradas para un evento dado, con su categoría, precio y stock remanente.
* **Namespace:** `http://f1.ticketing.soap/legacy`

#### Petición XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Header/>
   <soapenv:Body>
      <leg:consultarDisponibilidadRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
      </leg:consultarDisponibilidadRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Respuesta XML:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:consultarDisponibilidadResponse xmlns:ns2="http://f1.ticketing.soap/legacy">
         <ns2:gradas>
            <ns2:id>1</ns2:id>
            <ns2:codigoEvento>F1-2026-MAD</ns2:codigoEvento>
            <ns2:carrera>Madrid</ns2:carrera>
            <ns2:fechaCarrera>2026-09-11</ns2:fechaCarrera>
            <ns2:tribuna>Paddock Club Madrid</ns2:tribuna>
            <ns2:categoria>VIP</ns2:categoria>
            <ns2:precioUsd>3500.00</ns2:precioUsd>
            <ns2:stock>500</ns2:stock>
         </ns2:gradas>
         <!-- Demás tribunas del evento -->
      </ns2:consultarDisponibilidadResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

### B. `reservarEntradas(String codigoEvento, String tribuna, int cantidad)`
* **Propósito:** Valida el stock de la grada, aplica `@Transactional`, decrementa el stock en base de datos y retorna un código de confirmación alfanumérico (formato `TKT-XXXXX`).
* **Control de Errores:** Si `stock < cantidad` o no existe la grada, lanza `SinStockException` anotada con `@WebFault` / `@SoapFault`, traduciéndose en un **SOAP Fault estandarizado**.

#### Petición XML:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Header/>
   <soapenv:Body>
      <leg:reservarEntradasRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
         <leg:tribuna>Paddock Club Madrid</leg:tribuna>
         <leg:cantidad>2</leg:cantidad>
      </leg:reservarEntradasRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Respuesta Exitosa:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:reservarEntradasResponse xmlns:ns2="http://f1.ticketing.soap/legacy">
         <ns2:codigoConfirmacion>TKT-15615</ns2:codigoConfirmacion>
      </ns2:reservarEntradasResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### Respuesta SOAP Fault (Sin Stock):
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <SOAP-ENV:Fault>
         <faultcode>SOAP-ENV:Client</faultcode>
         <faultstring xml:lang="en">Stock insuficiente para realizar la reserva</faultstring>
      </SOAP-ENV:Fault>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

## 5. Endpoints y URLs de Acceso

| Recurso | URL | Descripción |
| :--- | :--- | :--- |
| **WSDL Oficial** | `http://localhost:8080/ws/ticketing.wsdl` | Contrato WSDL 1.1 generado automáticamente |
| **Endpoint SOAP** | `http://localhost:8080/ws/ticketing` | URL para el envío de sobres SOAP POST |
| **Consola H2** | `http://localhost:8080/h2-console` | Inspección web de base de datos (`JDBC URL: jdbc:h2:mem:f1_ticketing_db`, User: `sa`, Password en blanco) |

---

## 6. Ejecución y Pruebas

### Compilación y Test Unitarios:
```bash
mvn clean test
# O con wrapper:
./mvnw clean test
```

### Ejecutar la Aplicación:
```bash
mvn spring-boot:run
# O con wrapper:
./mvnw spring-boot:run
```

### Prueba rápida con cURL (Consultar Disponibilidad):
```bash
curl -X POST http://localhost:8080/ws/ticketing \
  -H "Content-Type: text/xml; charset=utf-8" \
  -d "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:leg=\"http://f1.ticketing.soap/legacy\"><soapenv:Body><leg:consultarDisponibilidadRequest><leg:codigoEvento>F1-2026-MAD</leg:codigoEvento></leg:consultarDisponibilidadRequest></soapenv:Body></soapenv:Envelope>"
```

---

## 7. Despliegue en la Nube (Render.com)

El proyecto incluye soporte nativo para despliegue en **Render** mediante contenedores Docker (`Dockerfile` multi-stage optimizado):

1. **Subir cambios a GitHub:**
   ```powershell
   git add .
   git commit -m "Configuración Docker para Render"
   git push origin main
   ```
2. **Crear Web Service en Render:**
   * Entra a [render.com](https://render.com) e inicia sesión con GitHub.
   * Haz clic en **New +** > **Web Service** > Conecta tu repositorio `Soap_Ticketing_F1`.
   * Parámetros: **Runtime:** `Docker`, **Plan:** `Free`.
   * Presiona **Deploy Web Service**.
3. **Acceso:**
   Una vez en estado `Live`, accede a:
   * **WSDL:** `https://tu-app.onrender.com/ws/ticketing.wsdl`
   * **SOAP Endpoint:** `https://tu-app.onrender.com/ws/ticketing`

> 📖 **Para la guía paso a paso con capturas y ejemplos, consulta la [Sección 11 de DOCUMENTACION_SISTEMA_LEGACY.md](file:///c:/Users/Martin/Documents/GitHub/Soap_Ticketing_F1/DOCUMENTACION_SISTEMA_LEGACY.md#11-gu%C3%ADa-de-despliegue-en-la-nube-rendercom).**

