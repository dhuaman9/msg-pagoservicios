package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.framework.pubsub.queue.publisher.MessagePublisher;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentPublisherAdapterTest {

    @Mock
    private MessagePublisher messagePublisher;

    @Test
    void billCompletedAdapter_shouldPublishEvent() {
        TransactionEvent event = new TransactionEvent();
        new BillCompletedPublisherAdapter(messagePublisher).publish(event);

        verify(messagePublisher, times(1)).publishMessage(event);
    }

    @Test
    void checkBillStatusAdapter_shouldPublishEvent() {
        TransactionEvent event = new TransactionEvent();
        new CheckBillStatusPublisherAdapter(messagePublisher).publish(event);

        verify(messagePublisher, times(1)).publishMessage(event);
    }
}
