package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.output.CheckBillStatusPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.framework.pubsub.queue.publisher.MessagePublisher;

@Component
public class CheckBillStatusPublisherAdapter implements CheckBillStatusPublisherPort {

    private final MessagePublisher messagePublisher;

    public CheckBillStatusPublisherAdapter(
        @Qualifier("checkBillStatusV2MessagePublisher") final MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void publish(final TransactionEvent event) {
        messagePublisher.publishMessage(event);
    }
}
