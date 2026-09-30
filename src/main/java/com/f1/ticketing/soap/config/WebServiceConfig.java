package com.f1.ticketing.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    /**
     * Configuración del MessageDispatcherServlet de Spring-WS para enrutar
     * las peticiones SOAP a través de la ruta base '/ws/*'.
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * Expone el contrato WSDL generado automáticamente en /ws/ticketing.wsdl
     * y mapea el endpoint a /ws/ticketing.
     */
    @Bean(name = "ticketing")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema ticketingSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("F1TicketingSoapPort");
        wsdl11Definition.setLocationUri("/ws/ticketing");
        wsdl11Definition.setTargetNamespace("http://f1.ticketing.soap/legacy");
        wsdl11Definition.setSchema(ticketingSchema);
        return wsdl11Definition;
    }

    /**
     * Carga el esquema XSD con las definiciones de tipos, operaciones y fallos.
     */
    @Bean
    public XsdSchema ticketingSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ticketing.xsd"));
    }
}
