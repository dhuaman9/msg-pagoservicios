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
public class PublisherBillCompletedV2Configuration {

    @Bean(destroyMethod = "terminate", name = "billCompletedV2PublishHandler")
    public PublisherHandler billCompletedV2PublishHandler(
        @NonNull final String projectId,
        @NonNull final CredentialsProvider credentialsProvider,
        @Value("${queue.publisher.payment.service.bill-completed.v2}") @NonNull final String topic) throws IOException {
        final ProjectTopicName projectTopicName = ProjectTopicName.of(projectId, topic);
        final Publisher publisher = Publisher.newBuilder(projectTopicName)
            .setCredentialsProvider(credentialsProvider)
            .setBatchingSettings(BatchingSettings.newBuilder()
                .setDelayThreshold(Duration.ofSeconds(30))
                .build())
            .build();
        return new PublisherHandler(publisher);
    }

    @Bean(name = "billCompletedV2MessagePublisher")
    public MessagePublisher billCompletedV2MessagePublisher(
        @NonNull final ObjectMapper objectMapper,
        @NonNull @Qualifier("billCompletedV2PublishHandler") final PublisherHandler publisherHandler) {
        return new MessagePublisher(publisherHandler, objectMapper);
    }
}
