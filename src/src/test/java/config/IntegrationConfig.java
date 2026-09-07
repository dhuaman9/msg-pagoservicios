package config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.gw.pagoservicios.util.authorization.InterbankAuthorizationManager;

@Configuration
public class IntegrationConfig {
    @MockBean
    private InterbankAuthorizationManager interbankAuthorizationManager;
}
