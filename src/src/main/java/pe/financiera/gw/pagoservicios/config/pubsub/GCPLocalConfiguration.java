package pe.financiera.gw.pagoservicios.config.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración GCP para el perfil {@code local}.
 *
 * <p>Reemplaza a {@link GCPConfiguration} (activa solo en {@code !local}) para poder levantar el
 * servicio contra el <b>emulador de Google Pub/Sub</b> sin credenciales reales.</p>
 *
 * <p>Además expone un {@link TransportChannelProvider} que apunta al emulador. A diferencia de los
 * admin clients, {@code Publisher}/{@code Subscriber} <b>no</b> detectan automáticamente
 * {@code PUBSUB_EMULATOR_HOST}, por lo que hay que pasarles este channel provider explícitamente.</p>
 */
@Configuration
@Profile("local")
public class GCPLocalConfiguration {

    @Value("${gcp.project-id:local-project}")
    private String projectId;

    @Value("${PUBSUB_EMULATOR_HOST:localhost:8085}")
    private String emulatorHost;

    @Bean
    public String projectId() {
        return projectId;
    }

    @Bean
    public CredentialsProvider gcpCredentials() {
        // Sin credenciales: el emulador ignora la autenticación.
        return NoCredentialsProvider.create();
    }

    @Bean(name = "pubsubEmulatorChannelProvider")
    public TransportChannelProvider pubsubEmulatorChannelProvider() {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(emulatorHost)
            .usePlaintext()
            .build();
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    }
}

