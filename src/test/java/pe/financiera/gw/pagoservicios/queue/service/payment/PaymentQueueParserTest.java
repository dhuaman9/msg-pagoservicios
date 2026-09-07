package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.Body;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.CommandTriggerEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.framework.event.base.message.third.party.Header;

import java.time.Clock;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.LIMA_ZONE;

public class PaymentQueueParserTest {

    private PaymentQueueParser parser;

    @BeforeEach
    public void setUp() {
        parser = new PaymentQueueParser(Clock.system(LIMA_ZONE));
    }

    @Test
    public void testCreateTransactionEvent_GivenValidInputs_ShouldReturnTransactionEvent() {
        final String eventTag = "EVENT_TAG", paymentId = "PAYMENT_ID", operationNumber = "OPERATION_NUMBER",
            errorMessage = "MESSAGE", errorCode = "CODE";

        final TransactionEvent parsed = parser.createTransactionEvent(eventTag, paymentId, operationNumber, errorMessage,
            errorCode);
        assertNotNull(parsed.getHeader().getEventId());
        assertEquals(CommandTriggerEnum.SERVICE_PAYMENT.name(), parsed.getHeader().getCommandTrigger());
        assertEquals(eventTag, parsed.getHeader().getEventTag());
        assertNotNull(parsed.getHeader().getTimestamp());

        assertEquals(operationNumber, parsed.getBody().getOperationNumber());
        assertNotNull(parsed.getBody().getCreationDate());

        assertEquals(paymentId, parsed.getCustomProperties().get("paymentId"));

        assertEquals(errorMessage, parsed.getError().getDescription());
        assertEquals(errorCode, parsed.getError().getCode());
    }

    @Test
    public void testCreateTransactionEvent_GivenNullError_ShouldReturnTransactionEvent() {
        final String eventTag = "EVENT_TAG", paymentId = "PAYMENT_ID", operationNumber = "OPERATION_NUMBER";

        final TransactionEvent parsed = parser.createTransactionEvent(eventTag, paymentId, operationNumber, null, null);
        assertNotNull(parsed.getHeader().getEventId());
        assertEquals(CommandTriggerEnum.SERVICE_PAYMENT.name(), parsed.getHeader().getCommandTrigger());
        assertEquals(eventTag, parsed.getHeader().getEventTag());
        assertNotNull(parsed.getHeader().getTimestamp());

        assertEquals(operationNumber, parsed.getBody().getOperationNumber());
        assertNotNull(parsed.getBody().getCreationDate());

        assertEquals(paymentId, parsed.getCustomProperties().get("paymentId"));

        assertNull(parsed.getError());
    }

    @Test
    public void testCreateTransactionEvent_GivenNullErrorMessage_ShouldReturnTransactionEvent() {
        final String eventTag = "EVENT_TAG", paymentId = "PAYMENT_ID", operationNumber = "OPERATION_NUMBER";

        final TransactionEvent parsed = parser.createTransactionEvent(eventTag, paymentId, operationNumber, null, "CODE");
        assertNotNull(parsed.getHeader().getEventId());
        assertEquals(CommandTriggerEnum.SERVICE_PAYMENT.name(), parsed.getHeader().getCommandTrigger());
        assertEquals(eventTag, parsed.getHeader().getEventTag());
        assertNotNull(parsed.getHeader().getTimestamp());

        assertEquals(operationNumber, parsed.getBody().getOperationNumber());
        assertNotNull(parsed.getBody().getCreationDate());

        assertEquals(paymentId, parsed.getCustomProperties().get("paymentId"));

        assertEquals("CODE", parsed.getError().getCode());
        assertNull(parsed.getError().getDescription());
    }

    @Test
    public void testCreateTransactionEvent_GivenNullErrorCode_ShouldReturnTransactionEvent() {
        final String eventTag = "EVENT_TAG", paymentId = "PAYMENT_ID", operationNumber = "OPERATION_NUMBER";

        final TransactionEvent parsed = parser.createTransactionEvent(eventTag, paymentId, operationNumber, "MESSAGE", null);
        assertNotNull(parsed.getHeader().getEventId());
        assertEquals(CommandTriggerEnum.SERVICE_PAYMENT.name(), parsed.getHeader().getCommandTrigger());
        assertEquals(eventTag, parsed.getHeader().getEventTag());
        assertNotNull(parsed.getHeader().getTimestamp());

        assertEquals(operationNumber, parsed.getBody().getOperationNumber());
        assertNotNull(parsed.getBody().getCreationDate());

        assertEquals(paymentId, parsed.getCustomProperties().get("paymentId"));

        assertEquals("MESSAGE", parsed.getError().getDescription());
        assertNull(parsed.getError().getCode());
    }

    @Test
    public void testUpdateTransactionEvent_GivenValidInputs_ShouldReturnUpdatedTransactionEvent() {
        final String paymentId = "PAYMENT_ID";
        final TransactionEvent event = new TransactionEvent();
        event.setCustomProperties(new HashMap<>());
        event.setHeader(new Header());
        event.setBody(new Body());

        final TransactionEvent parsed = parser.updateTransactionEvent(event, paymentId);
        assertEquals(paymentId, parsed.getCustomProperties().get("paymentId"));
    }
}
