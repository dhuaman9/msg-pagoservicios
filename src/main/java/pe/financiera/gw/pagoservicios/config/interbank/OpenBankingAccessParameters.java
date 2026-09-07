package pe.financiera.gw.pagoservicios.config.interbank;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;

@Component
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "services-api.interbank.access")
public class OpenBankingAccessParameters {

    private HashMap<String, String> subscriptionKey;

    private String applicationId;

    private String password;

    private String grantType;

    public String getBasicAuth() {
        String authorization = Base64.getEncoder().encodeToString(
            applicationId
                .concat(":")
                .concat(password).getBytes()
        );
        return "Basic ".concat(authorization);
    }

    public String getSubscriptionKeyOf(String subscription) {
        return subscriptionKey.get(subscription);
    }
}
