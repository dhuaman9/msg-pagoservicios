package pe.financiera.gw.pagoservicios.config.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@Profile("!local")
public class GCPConfiguration {

    @Value("${gcp.credentials}")
    private String encodedCredentials;

    @Value("${gcp.project-id}")
    private String projectId;

    @Bean
    public String projectId() {
        return projectId;
    }

    @Bean
    public CredentialsProvider gcpCredentials() throws IOException {
        byte[] decodeBytes = Base64.getDecoder().decode(encodedCredentials);
        InputStream inputStream = new ByteArrayInputStream(decodeBytes);
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        return FixedCredentialsProvider.create(credentials);
    }

}
