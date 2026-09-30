# Manual de Integración y Documentación Oficial
## Sistema Legacy de Ticketería F1 (`f1-ticketing-legacy-soap`)

---

## 1. Introducción y Propósito del Sistema

El sistema **`f1-ticketing-legacy-soap`** opera como el subsistema central y proveedor oficial de gestión de inventario y reservas de entradas para los Grandes Premios de la **Fórmula 1**. 

Al tratarse de una plataforma corporativa de naturaleza **Legacy**, todos los intercambios de información hacia sistemas externos, canales de venta y pasarelas de comercio electrónico se realizan de manera estricta mediante el protocolo estándar **XML/SOAP 1.1** sobre HTTP.

### Características Principales:
* **Protocolo Estricto:** XML / SOAP 1.1 con validación basada en esquemas XSD y WSDL 1.1.
* **Transaccionalidad ACID:** Operaciones de reserva atómicas protegidas mediante bloqueos transaccionales a nivel de base de datos (`@Transactional`).
* **Base de Datos Integrada:** Motor relacional H2 en memoria con inicialización masiva de inventario a partir del catálogo oficial de la temporada.
* **Manejo Estándar de Fallos:** Respuestas de error tipificadas mediante **SOAP Faults** (`<SOAP-ENV:Fault>`).

---

## 2. Arquitectura y Stack Tecnológico

El microservicio está construido bajo los siguientes estándares de la industria:

* **Lenguaje:** Java 17 LTS o superior (validado en JDK 24).
* **Framework:** Spring Boot 3.3.4.
* **Pila SOAP:** Spring Web Services 4.0 (`spring-boot-starter-web-services`) + WSDL4J 1.6.3.
* **Anotaciones de Contrato:** Estándar Jakarta EE (`jakarta.jws.*`, `jakarta.xml.ws.*`, `jakarta.xml.bind.*`).
* **Persistencia:** Spring Data JPA con Hibernate 6.5 (`spring-boot-starter-data-jpa`).
* **Base de Datos:** H2 Database Engine 2.2 en memoria.

```
+-------------------------------------------------------------------------+
|                              CLIENTES SOAP                              |
|           (Canal Web, Agencias, Postman, SoapUI, Scripts)               |
+-------------------------------------------------------------------------+
                                     |  SOAP / XML (HTTP POST)
                                     v
+-------------------------------------------------------------------------+
|                  Servlet: /ws/* (MessageDispatcherServlet)              |
|                     Endpoint Base: /ws/ticketing                        |
|                     Contrato WSDL: /ws/ticketing.wsdl                   |
+-------------------------------------------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                      CAPA DE ENRUTAMIENTO SOAP                          |
|             F1TicketingEndpoint (@Endpoint, @PayloadRoot)               |
+-------------------------------------------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                       CAPA DE SERVICIO DE NEGOCIO                       |
|           F1TicketingSoapService (@WebService / @WebMethod)             |
|                  F1TicketingSoapServiceImpl (@Transactional)             |
+-------------------------------------------------------------------------+
                    |                                 |
                    | (Si stock < cantidad)           v
                    v                    +--------------------------------+
         SinStockException (@WebFault)   |  InventarioGradaRepository     |
             SOAP Fault Generator        +--------------------------------+
                                                      |
                                                      v
                                         +--------------------------------+
                                         | Base de Datos H2 (En Memoria)  |
                                         |     Tabla: inventario_grada    |
                                         +--------------------------------+
```

---

## 3. Modelo de Datos del Inventario

La tabla **`inventario_grada`** modela la oferta comercial y disponibilidad de asientos en cada circuito:

| Columna | Tipo de Dato | Nullable | Descripción |
| :--- | :--- | :---: | :--- |
| `id` | `BIGINT (IDENTITY)` | NO | Clave primaria autoincremental única por grada. |
| `codigo_evento` | `VARCHAR(50)` | NO | Identificador único del Gran Premio (ej. `F1-2026-MAD`). |
| `carrera` | `VARCHAR(100)` | NO | Nombre o sede del Gran Premio (ej. `Madrid`, `São Paulo`). |
| `fecha_carrera`| `DATE` | NO | Fecha oficial de la carrera en formato ISO (`YYYY-MM-DD`). |
| `tribuna` | `VARCHAR(100)` | NO | Nombre oficial de la tribuna o sector del autódromo. |
| `categoria` | `VARCHAR(50)` | NO | Clasificación del asiento: `VIP`, `Premium`, `Standard`, `General`. |
| `precio_usd` | `DECIMAL(10,2)` | NO | Tarifa unitaria oficial expresada en dólares estadounidenses (USD). |
| `stock` | `INT` | NO | Cantidad física de localidades actualmente disponibles para reserva. |

---

## 4. Catálogo de Grandes Premios y Eventos Disponibles

El sistema cuenta con **76 tribunas activas** distribuidas en los **19 Grandes Premios** precargados desde el inventario oficial:

| Código de Evento | Gran Premio / Ciudad | Fecha Oficial | Tribunas Disponibles |
| :--- | :--- | :---: | :--- |
| **`F1-2026-MAD`** | Madrid | 2026-09-11 | Paddock Club Madrid, Tribuna Principal, Grada T4, Pelouse |
| **`F1-2026-BAK`** | Bakú | 2026-09-25 | Paddock Club Azerbaijan, Absheron Grandstand, Azneft Grandstand, General Admission |
| **`F1-2026-SIN`** | Singapur | 2026-10-09 | Singapore Paddock Club, Pit Grandstand, Padang Grandstand, Premier Walkabout |
| **`F1-2026-AUS`** | Austin | 2026-10-23 | Paddock Club COTA, Main Grandstand, Turn 12 Grandstand, General Admission |
| **`F1-2026-MEX`** | Ciudad de México | 2026-10-30 | Paddock Club Mexico, Grada 1 (Principal), Foro Sol Norte, Admisión General |
| **`F1-2026-SAO`** | São Paulo | 2026-11-06 | VIP Club Interlagos, Grandstand B (Recta), Grandstand M (Senna S), Sector G (General) |
| **`F1-2026-LAS`** | Las Vegas | 2026-11-19 | Bellagio Fountain Club, East Harmon Zone, MSG Sphere Zone, T-Mobile Zone General |
| **`F1-2026-QAT`** | Lusail | 2026-11-27 | Lusail Club Lounge, Main Grandstand, North Grandstand, General Admission Lusail |
| **`F1-2026-ABU`** | Abu Dabi | 2026-12-04 | Yas Suites Main, West Grandstand, North Grandstand, Abu Dhabi Hill (General) |
| **`F1-2027-BAH`** | Bahréin | 2027-03-14 | Bahrain Paddock Club, Main Grandstand, Batelco Grandstand, Victory Grandstand |
| **`F1-2027-SAU`** | Arabia Saudita | 2027-03-21 | Jeddah Paddock Club, Main Grandstand, Central Grandstand, General Admission Jeddah |
| **`F1-2027-MEL`** | Australia | 2027-04-04 | Albert Park Lounge, Fangio Grandstand, Brabham Grandstand, Park Pass (General) |
| **`F1-2027-SUZ`** | Japón | 2027-04-11 | Suzuka Paddock Club, Grandstand V2, Grandstand B2, West Area General |
| **`F1-2027-SHA`** | China | 2027-04-18 | Shanghai Paddock Club, Main Grandstand A, Grandstand H, Grass Area General |
| **`F1-2027-MIA`** | Miami | 2027-05-02 | Hard Rock Beach Club, Start/Finish Grandstand, Marina Grandstand, Campus Pass (General) |
| **`F1-2027-MON`** | Canadá | 2027-05-23 | Elite Restaurant Gilles Villeneuve, Grandstand 1 (Start/Finish), Grandstand 15 (Hairpin), General Admission |
| **`F1-2027-MCO`** | Mónaco | 2027-06-06 | Yacht Hospitality Port Hercule, Grandstand K (Harbour), Grandstand T (Piscine), Rocher (General) |
| **`F1-2027-POR`** | Portugal | 2027-06-20 | Paddock Club Portimão, Bancada Principal, Bancada Portimão, Peão (General) |
| **`F1-2027-SIL`** | Gran Bretaña | 2027-07-04 | Silverstone Six Hospitality, Abbey Grandstand, Becketts Grandstand, General Admission |

---

## 5. Endpoints y Puntos de Acceso

| Recurso | URL | Método | Descripción |
| :--- | :--- | :---: | :--- |
| **Contrato WSDL** | `http://localhost:8080/ws/ticketing.wsdl` | `GET` | Definición completa WSDL 1.1 del servicio. |
| **Servicio SOAP** | `http://localhost:8080/ws/ticketing` | `POST` | Punto de entrada para enviar solicitudes SOAP. |
| **Consola H2 Web** | `http://localhost:8080/h2-console` | `GET/POST` | Interfaz gráfica para auditoría de la base de datos. |

---

## 6. Paso a Paso: Cómo Buscar Entradas (Consultar Disponibilidad)

Esta operación permite conocer la oferta disponible para una carrera: lista todas las gradas existentes, sus categorías de acceso, precio en dólares y el número exacto de localidades aún disponibles en inventario.

### Flujo de la Operación:
1. El cliente envía el código del evento deseado (ej. `F1-2026-MAD`).
2. El sistema consulta la base de datos `inventario_grada`.
3. Retorna un conjunto de elementos `<gradas>` con el desglose de cada sector.

---

### Paso 6.1: Preparar la Solicitud XML
Construye el sobre SOAP con la operación **`consultarDisponibilidadRequest`**:

* **Endpoint:** `http://localhost:8080/ws/ticketing`
* **HTTP Method:** `POST`
* **Header:** `Content-Type: text/xml; charset=utf-8`

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

---

### Paso 6.2: Ejecución según tu herramienta

#### Opción Postman:
1. Crea una nueva petición tipo **POST** hacia `http://localhost:8080/ws/ticketing`.
2. En la pestaña **Headers**, agrega `Content-Type` con valor `text/xml; charset=utf-8`.
3. En la pestaña **Body**, selecciona **raw** y cambia el tipo a **XML**.
4. Pega el XML anterior y presiona **Send**.

#### Opción Terminal PowerShell (Windows):
```powershell
$body = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Body>
      <leg:consultarDisponibilidadRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
      </leg:consultarDisponibilidadRequest>
   </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-RestMethod -Uri "http://localhost:8080/ws/ticketing" -Method Post -ContentType "text/xml; charset=utf-8" -Body $body
```

#### Opción cURL:
```bash
curl -X POST http://localhost:8080/ws/ticketing \
  -H "Content-Type: text/xml; charset=utf-8" \
  -d "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:leg=\"http://f1.ticketing.soap/legacy\"><soapenv:Body><leg:consultarDisponibilidadRequest><leg:codigoEvento>F1-2026-MAD</leg:codigoEvento></leg:consultarDisponibilidadRequest></soapenv:Body></soapenv:Envelope>"
```

---

### Paso 6.3: Estructura de la Respuesta Obtenida
El servicio responde con código HTTP `200 OK` y el listado detallado de gradas:

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
         <ns2:gradas>
            <ns2:id>2</ns2:id>
            <ns2:codigoEvento>F1-2026-MAD</ns2:codigoEvento>
            <ns2:carrera>Madrid</ns2:carrera>
            <ns2:fechaCarrera>2026-09-11</ns2:fechaCarrera>
            <ns2:tribuna>Tribuna Principal</ns2:tribuna>
            <ns2:categoria>Premium</ns2:categoria>
            <ns2:precioUsd>1200.00</ns2:precioUsd>
            <ns2:stock>2500</ns2:stock>
         </ns2:gradas>
         <ns2:gradas>
            <ns2:id>3</ns2:id>
            <ns2:codigoEvento>F1-2026-MAD</ns2:codigoEvento>
            <ns2:carrera>Madrid</ns2:carrera>
            <ns2:fechaCarrera>2026-09-11</ns2:fechaCarrera>
            <ns2:tribuna>Grada T4</ns2:tribuna>
            <ns2:categoria>Standard</ns2:categoria>
            <ns2:precioUsd>550.00</ns2:precioUsd>
            <ns2:stock>5000</ns2:stock>
         </ns2:gradas>
         <ns2:gradas>
            <ns2:id>4</ns2:id>
            <ns2:codigoEvento>F1-2026-MAD</ns2:codigoEvento>
            <ns2:carrera>Madrid</ns2:carrera>
            <ns2:fechaCarrera>2026-09-11</ns2:fechaCarrera>
            <ns2:tribuna>Pelouse</ns2:tribuna>
            <ns2:categoria>General</ns2:categoria>
            <ns2:precioUsd>200.00</ns2:precioUsd>
            <ns2:stock>12000</ns2:stock>
         </ns2:gradas>
      </ns2:consultarDisponibilidadResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

## 7. Paso a Paso: Cómo Reservar Entradas

Esta operación efectúa una reserva formal de localidades con actualización transaccional en la base de datos.

### Flujo de la Operación:
1. El cliente envía el código del evento (`codigoEvento`), el nombre de la tribuna (`tribuna`) y la cantidad de boletos a comprar (`cantidad`).
2. El sistema localiza la grada específica en el inventario.
3. Se valida que `cantidad > 0` y que `stock >= cantidad`.
4. **Si hay stock suficiente:**
   * Se descuenta la cantidad reservada (`stock = stock - cantidad`).
   * Se guarda el cambio de forma transaccional en la base de datos (`@Transactional`).
   * Se genera un código alfanumérico único de confirmación (ej. `TKT-48291`).
   * Se devuelve el código al cliente.
5. **Si NO hay stock suficiente:**
   * Se interrumpe la transacción.
   * Se lanza `SinStockException`.
   * El cliente recibe un **SOAP Fault** estandarizado.

---

### Paso 7.1: Preparar la Solicitud XML de Reserva
Construye el sobre SOAP con la operación **`reservarEntradasRequest`**:

* **Endpoint:** `http://localhost:8080/ws/ticketing`
* **HTTP Method:** `POST`
* **Header:** `Content-Type: text/xml; charset=utf-8`

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Header/>
   <soapenv:Body>
      <leg:reservarEntradasRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
         <leg:tribuna>Paddock Club Madrid</leg:tribuna>
         <leg:cantidad>4</leg:cantidad>
      </leg:reservarEntradasRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

---

### Paso 7.2: Ejecución de la Reserva

#### Opción Postman:
1. Configura una petición **POST** hacia `http://localhost:8080/ws/ticketing`.
2. Header: `Content-Type: text/xml; charset=utf-8`.
3. Body: Pega el XML anterior y pulsa **Send**.

#### Opción PowerShell:
```powershell
$body = @"
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Body>
      <leg:reservarEntradasRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
         <leg:tribuna>Paddock Club Madrid</leg:tribuna>
         <leg:cantidad>4</leg:cantidad>
      </leg:reservarEntradasRequest>
   </soapenv:Body>
</soapenv:Envelope>
"@

Invoke-RestMethod -Uri "http://localhost:8080/ws/ticketing" -Method Post -ContentType "text/xml; charset=utf-8" -Body $body
```

---

### Paso 7.3: Respuesta Exitosa (Reserva Confirmada)
El microservicio responde con código `200 OK` y el código de ticket oficial:

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:reservarEntradasResponse xmlns:ns2="http://f1.ticketing.soap/legacy">
         <ns2:codigoConfirmacion>TKT-38192</ns2:codigoConfirmacion>
      </ns2:reservarEntradasResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

> **Efecto en Inventario:** La tribuna *Paddock Club Madrid* que contaba inicialmente con `500` entradas ahora pasa automáticamente a tener `496` asientos disponibles.

---

### Paso 7.4: Respuesta de Error por Stock Insuficiente (SOAP Fault)
Si se solicita una cantidad superior al stock remanente (por ejemplo, `999999` entradas):

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:leg="http://f1.ticketing.soap/legacy">
   <soapenv:Header/>
   <soapenv:Body>
      <leg:reservarEntradasRequest>
         <leg:codigoEvento>F1-2026-MAD</leg:codigoEvento>
         <leg:tribuna>Paddock Club Madrid</leg:tribuna>
         <leg:cantidad>999999</leg:cantidad>
      </leg:reservarEntradasRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

El servidor rechaza la solicitud retornando código HTTP `500 Internal Server Error` con el XML estándar del **SOAP Fault**:

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

## 8. Catálogo de Errores y Excepciones

Las validaciones de negocio gestionadas por [`SinStockException`](file:///c:/Users/Martin/Documents/GitHub/Soap_Ticketing_F1/src/main/java/com/f1/ticketing/soap/exception/SinStockException.java) generan fallos controlados bajo las siguientes condiciones:

| Escenario de Error | Causa | Código de Error Interno | Resultado SOAP |
| :--- | :--- | :--- | :--- |
| **Stock Insuficiente** | `cantidad > stock` disponible | `ERR-STOCK-INSUFICIENTE` | SOAP Fault (`Client`) |
| **Tribuna No Encontrada** | La tribuna solicitada no existe en el circuito | `ERR-TRIBUNA-NO-ENCONTRADA` | SOAP Fault (`Client`) |
| **Cantidad Inválida** | `cantidad <= 0` | `ERR-CANTIDAD-INVALIDA` | SOAP Fault (`Client`) |
| **Datos Incompletos** | Evento o tribuna en blanco / nulos | `ERR-DATOS-INCOMPLETOS` | SOAP Fault (`Client`) |

---

## 9. Auditoría y Comprobación en Base de Datos (Consola H2)

Para verificar visualmente la base de datos y auditar cómo se actualiza el stock:

1. Inicia la aplicación con `.\mvnw.cmd spring-boot:run`.
2. Ingresa desde el navegador a: [http://localhost:8080/h2-console](http://localhost:8080/h2-console).
3. Configura los parámetros de conexión:
   * **Driver Class:** `org.h2.Driver`
   * **JDBC URL:** `jdbc:h2:mem:f1_ticketing_db`
   * **User Name:** `sa`
   * **Password:** *(dejar vacío)*
4. Presiona **Connect**.
5. Ejecuta consultas SQL directas para monitorear el estado:

```sql
-- Consultar el stock actual de una carrera específica:
SELECT id, codigo_evento, carrera, tribuna, categoria, precio_usd, stock
FROM inventario_grada
WHERE codigo_evento = 'F1-2026-MAD';

-- Identificar tribunas con mayor demanda:
SELECT carrera, tribuna, categoria, stock, precio_usd
FROM inventario_grada
ORDER BY stock ASC;
```

---

## 10. Resumen de Comandos de Operación

| Acción | Comando |
| :--- | :--- |
| **Ejecutar Tests Automáticos** | `.\mvnw.cmd test` |
| **Arrancar Microservicio** | `.\mvnw.cmd spring-boot:run` |
| **Generar Paquete JAR** | `.\mvnw.cmd clean package -DskipTests` |
