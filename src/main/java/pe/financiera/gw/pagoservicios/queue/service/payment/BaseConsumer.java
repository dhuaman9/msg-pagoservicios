package pe.financiera.gw.pagoservicios.queue.service.payment;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import lombok.extern.slf4j.Slf4j;
import pe.financiera.framework.event.base.message.third.party.Header;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.framework.pubsub.queue.publisher.MessagePublisher;
import pe.financiera.framework.pubsub.queue.publisher.PublisherHandler;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class BaseConsumer {

    protected static final int INDRA_PLATFORM = 1;
    protected static final int IBK_PLATFORM = 2;

    public String getEventTag(TransactionEvent event) {
        return Optional.ofNullable(event)
            .map(TransactionEvent::getHeader)
            .map(Header::getEventTag)
            .orElse(null);

    }

    public String getCommandTrigger(TransactionEvent event) {
        return Optional.ofNullable(event)
            .map(TransactionEvent::getHeader)
            .map(Header::getCommandTrigger)
            .orElse(null);
    }

    public boolean validateNumberOfRetry(int nbRetry) {
        boolean ret = true;
        if (nbRetry != 0) {
            log.warn("Indra event should not be retried, retry number: {}", nbRetry);
            ret = false;
        }
        return ret;
    }

    public void publishToTopic(PublisherWrapper publisherWrapper) throws IOException {
        final ProjectTopicName projectTopicName = ProjectTopicName.of(publisherWrapper.getProjectId(), publisherWrapper.getDynamicTopic());
        final Publisher publisher = Publisher
            .newBuilder(projectTopicName)
            .setCredentialsProvider(publisherWrapper.getCredentialsProvider())
            .build();

        final MessagePublisher messagePublisher = new MessagePublisher(new PublisherHandler(publisher), publisherWrapper.getObjectMapper());
        messagePublisher.publishMessage(publisherWrapper.getMessage());
    }
}
