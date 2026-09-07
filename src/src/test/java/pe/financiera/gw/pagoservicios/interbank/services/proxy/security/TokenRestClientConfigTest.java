package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pe.financiera.gw.pagoservicios.config.retrofit.RetrofitRestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenRestClientConfigTest {
    private final RetrofitRestClient retrofitRestClient = new RetrofitRestClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void customerRestClient() {
        retrofitRestClient.setBaseUrl("http://localhost/");
        retrofitRestClient.setConnectTimeout(100L);
        retrofitRestClient.setMaxRequest(100);
        retrofitRestClient.setReadTimeout(100L);
        retrofitRestClient.setWriteTimeout(100L);
        TokenRestClientConfig tokenRestClientConfig = new TokenRestClientConfig(retrofitRestClient);
        TokenRestClient tokenRestClient = tokenRestClientConfig.customerRestClient(objectMapper);
        assertNotNull(tokenRestClient);
    }
}
