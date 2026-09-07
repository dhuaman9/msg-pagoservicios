package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillCompletedPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.framework.pubsub.queue.publisher.MessagePublisher;

@Component
public class BillCompletedPublisherAdapter implements BillCompletedPublisherPort {

    private final MessagePublisher messagePublisher;

    public BillCompletedPublisherAdapter(
        @Qualifier("billCompletedV2MessagePublisher") final MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void publish(final TransactionEvent event) {
        messagePublisher.publishMessage(event);
    }
}
