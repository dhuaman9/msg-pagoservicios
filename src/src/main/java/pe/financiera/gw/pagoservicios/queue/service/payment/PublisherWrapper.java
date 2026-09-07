package pe.financiera.gw.pagoservicios.queue.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.financiera.framework.pubsub.messaging.Message;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublisherWrapper {
    private String projectId;
    private String dynamicTopic;
    private CredentialsProvider credentialsProvider;
    private ObjectMapper objectMapper;
    private Message message;
}
