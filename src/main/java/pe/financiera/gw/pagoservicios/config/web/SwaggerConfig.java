package pe.financiera.gw.pagoservicios.config.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String CONTEXT_PATH = "/gwpagoservicios";

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> operation;
    }

    @Bean
    public OpenAPI api() {
        final Server localServer = new Server();
        localServer.setUrl(CONTEXT_PATH);
        localServer.setDescription("Context path del gateway");

        final Contact contact = new Contact();
        contact.setEmail("no-reply@servicioalcliente.sip.pe");
        contact.setName("Financiera Oh");

        final Info info = new Info()
            .title("gwpagoservicios")
            .version("1.0.0")
            .contact(contact)
            .description("Especificación REST del gateway de pago de servicios Interbank")
            .termsOfService("https://tarjetaoh.pe/");

        return new OpenAPI().info(info).servers(List.of(localServer));
    }
}
