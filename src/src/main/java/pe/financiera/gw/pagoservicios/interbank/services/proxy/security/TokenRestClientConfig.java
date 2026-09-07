package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.gw.pagoservicios.config.retrofit.RetrofitRestClient;

@Configuration
@Slf4j
public class TokenRestClientConfig {

    private final RetrofitRestClient retrofitRestClient;

    @Autowired
    public TokenRestClientConfig(RetrofitRestClient retrofitRestClient) {
        this.retrofitRestClient = retrofitRestClient;
    }

    @Bean
    public TokenRestClient customerRestClient(ObjectMapper objectMapper) {
        return retrofitRestClient.restClient(objectMapper, TokenRestClient.class);
    }
}
