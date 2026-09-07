package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.financiera.gw.pagoservicios.config.retrofit.RetrofitRestClient;

@Configuration
@Slf4j
public class BillPaymentRestClientConfig {

    private final RetrofitRestClient retrofitRestClient;

    @Autowired
    public BillPaymentRestClientConfig(RetrofitRestClient retrofitRestClient) {
        this.retrofitRestClient = retrofitRestClient;
    }

    @Bean
    public BillPaymentRestClient billPaymentRestClient(ObjectMapper objectMapper) {
        return retrofitRestClient.restClient(objectMapper, BillPaymentRestClient.class);
    }
}
