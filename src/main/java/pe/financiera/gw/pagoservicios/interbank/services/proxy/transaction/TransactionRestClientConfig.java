package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.gw.pagoservicios.config.retrofit.RetrofitRestClient;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.TokenRestClient;

@Configuration
public class TransactionRestClientConfig {

    private final RetrofitRestClient retrofitRestClient;

    @Autowired
    public TransactionRestClientConfig(RetrofitRestClient retrofitRestClient) {
        this.retrofitRestClient = retrofitRestClient;
    }

    @Bean
    public TransactionRestClient transactionRestClient(ObjectMapper objectMapper) {
        return retrofitRestClient.restClient(objectMapper, TransactionRestClient.class);
    }
}
