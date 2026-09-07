package pe.financiera.gw.pagoservicios.config.pubsub.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threeten.bp.Duration;
import pe.financiera.framework.pubsub.queue.publisher.MessagePublisher;
import pe.financiera.framework.pubsub.queue.publisher.PublisherHandler;

import java.io.IOException;

@Configuration
public class PublisherCheckBillStatusV2Configuration {

    @Bean(destroyMethod = "terminate", name = "checkBillStatusV2PublishHandler")
    public PublisherHandler checkBillStatusV2PublishHandler(
        @NonNull final String projectId,
        @NonNull final CredentialsProvider credentialsProvider,
        @Value("${queue.publisher.payment.service.check.bill.status.v2}") @NonNull final String topic) throws IOException {
        final ProjectTopicName projectTopicName = ProjectTopicName.of(projectId, topic);
        final Publisher publisher = Publisher.newBuilder(projectTopicName)
            .setCredentialsProvider(credentialsProvider)
            .setBatchingSettings(BatchingSettings.newBuilder()
                .setDelayThreshold(Duration.ofSeconds(30))
                .build())
            .build();
        return new PublisherHandler(publisher);
    }

    @Bean(name = "checkBillStatusV2MessagePublisher")
    public MessagePublisher checkBillStatusV2MessagePublisher(
        @NonNull final ObjectMapper objectMapper,
        @NonNull @Qualifier("checkBillStatusV2PublishHandler") final PublisherHandler publisherHandler) {
        return new MessagePublisher(publisherHandler, objectMapper);
    }
}
