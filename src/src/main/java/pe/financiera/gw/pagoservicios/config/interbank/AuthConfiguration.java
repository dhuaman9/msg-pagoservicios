package pe.financiera.gw.pagoservicios.config.interbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.gw.pagoservicios.util.authorization.JWTParser;

@Configuration
public class AuthConfiguration {

    @Bean
    public JWTParser jwtParser(ObjectMapper objectMapper) {
        return new JWTParser(objectMapper);
    }
}
