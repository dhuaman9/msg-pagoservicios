package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import pe.financiera.framework.event.base.message.third.party.Header;
import pe.financiera.framework.pubsub.messaging.exception.NeededRetryException;
import pe.financiera.gw.pagoservicios.interbank.business.input.CheckBillStatusService;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CheckBillStatusV2ConsumerTest {

    @Mock
    private CheckBillStatusService checkBillStatusService;

    private CheckBillStatusV2Consumer consumer;

    private TransactionEvent event;

    @BeforeEach
    void setUp() {
        consumer = new CheckBillStatusV2Consumer(checkBillStatusService);

        event = new TransactionEvent();
        Header header = new Header();
        header.setEventId("EV-1");
        header.setTransactionId("TX-1");
        header.setEventTag("SERVICE_PAYMENT_COMPLETED");
        header.setCommandTrigger("SERVICE_PAYMENT");
        event.setHeader(header);
    }

    @Test
    void accept_shouldDelegateToService() throws Exception {
        consumer.accept(event, 0);

        verify(checkBillStatusService, times(1)).checkBillStatusV2(event, 0);
    }

    @Test
    void accept_shouldRethrowNeededRetryException() throws Exception {
        doThrow(new NeededRetryException()).when(checkBillStatusService).checkBillStatusV2(event, 1);

        assertThrows(NeededRetryException.class, () -> consumer.accept(event, 1));
    }

    @Test
    void accept_shouldSwallowOtherExceptions() throws Exception {
        doThrow(new InterbankApiException("07.01.03", "BillNotFoundException", HttpStatus.NOT_FOUND))
            .when(checkBillStatusService).checkBillStatusV2(event, 2);

        assertDoesNotThrow(() -> consumer.accept(event, 2));
    }

    @Test
    void baseConsumer_shouldExposeHeaderValues() {
        assertEquals("SERVICE_PAYMENT_COMPLETED", consumer.getEventTag(event));
        assertEquals("SERVICE_PAYMENT", consumer.getCommandTrigger(event));
    }

    @Test
    void baseConsumer_shouldReturnNullWhenEventIsMissing() {
        assertNull(consumer.getEventTag(null));
        assertNull(consumer.getCommandTrigger(null));
    }

    @Test
    void baseConsumer_shouldValidateNumberOfRetry() {
        assertTrue(consumer.validateNumberOfRetry(0));
        assertFalse(consumer.validateNumberOfRetry(1));
    }
}
