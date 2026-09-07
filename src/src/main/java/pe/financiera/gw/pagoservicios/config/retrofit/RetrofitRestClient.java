package pe.financiera.gw.pagoservicios.config.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "services-api.interbank.api")
@Component
public class RetrofitRestClient {
    private String baseUrl;
    private long connectTimeout;
    private long readTimeout;
    private long writeTimeout;
    private int maxRequest;

    public <T> T restClient(
        ObjectMapper objectMapper,
        Class<T> retrofitInterface) {

        return new Retrofit.Builder()
            .client(new RestClientConfig()
                .getHttpClient(maxRequest, connectTimeout,
                    readTimeout, writeTimeout))
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper)).build()
            .create(retrofitInterface);
    }
}
