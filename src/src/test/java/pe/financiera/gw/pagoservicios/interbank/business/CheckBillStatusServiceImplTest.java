package pe.financiera.gw.pagoservicios.interbank.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pe.financiera.framework.event.base.message.third.party.Header;
import pe.financiera.framework.pubsub.messaging.exception.NeededRetryException;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatus;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillCompletedPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.business.output.TransactionPort;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.Body;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.IncorporateEventTagEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.queue.service.payment.PaymentQueueParser;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.LIMA_ZONE;

@ExtendWith(MockitoExtension.class)
class CheckBillStatusServiceImplTest {

    private static final String OPERATION_NUMBER = "OP-123";
    private static final String PAYMENT_ID = "PAY-123";

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private PaymentQueueParser paymentQueueParser;

    @Mock
    private BillCompletedPublisherPort billCompletedPublisherPort;

    private CheckBillStatusServiceImpl checkBillStatusService;

    @BeforeEach
    void setUp() {
        checkBillStatusService = new CheckBillStatusServiceImpl(
            transactionPort, paymentQueueParser, billCompletedPublisherPort, paymentQueueParser, Clock.system(LIMA_ZONE));
        ReflectionTestUtils.setField(checkBillStatusService, "maxRetries", 3);
    }

    @Test
    void checkBillStatusV2_shouldRetryWhenTtlNotReached() {
        TransactionEvent event = buildEvent(epochMillisFromNow(60));

        assertThrows(NeededRetryException.class, () -> checkBillStatusService.checkBillStatusV2(event, 0));

        verify(billCompletedPublisherPort, never()).publish(any());
    }

    @Test
    void checkBillStatusV2_shouldPublishCompletedWhenSettled() throws Exception {
        TransactionEvent event = buildEvent(epochMillisFromNow(-60));
        when(transactionPort.getBillStatus(OPERATION_NUMBER)).thenReturn(billStatus(BillStatus.SETTLED.name(), null));
        when(paymentQueueParser.updateTransactionEvent(event, PAYMENT_ID)).thenReturn(event);

        checkBillStatusService.checkBillStatusV2(event, 0);

        verify(billCompletedPublisherPort, times(1)).publish(event);
    }

    @Test
    void checkBillStatusV2_shouldPublishFailedEventWhenFailed() throws Exception {
        TransactionEvent event = buildEvent(epochMillisFromNow(-60));
        BillStatusEntity.Error error = BillStatusEntity.Error.builder()
            .message("BillNotFoundException")
            .code("07.01.03")
            .build();
        TransactionEvent failedEvent = new TransactionEvent();
        when(transactionPort.getBillStatus(OPERATION_NUMBER)).thenReturn(billStatus(BillStatus.FAILED.name(), error));
        when(paymentQueueParser.createTransactionEvent(
            IncorporateEventTagEnum.FAILED.name(), PAYMENT_ID, OPERATION_NUMBER, "BillNotFoundException", "07.01.03"))
            .thenReturn(failedEvent);

        checkBillStatusService.checkBillStatusV2(event, 0);

        verify(billCompletedPublisherPort, times(1)).publish(failedEvent);
    }

    @Test
    void checkBillStatusV2_shouldRetryWhenStatusPendingAndRetriesLeft() throws Exception {
        TransactionEvent event = buildEvent(epochMillisFromNow(-60));
        when(transactionPort.getBillStatus(OPERATION_NUMBER)).thenReturn(billStatus("PENDING", null));

        assertThrows(NeededRetryException.class, () -> checkBillStatusService.checkBillStatusV2(event, 1));

        verify(billCompletedPublisherPort, never()).publish(any());
    }

    @Test
    void checkBillStatusV2_shouldStopWhenStatusPendingAndRetriesExhausted() throws Exception {
        TransactionEvent event = buildEvent(epochMillisFromNow(-60));
        when(transactionPort.getBillStatus(OPERATION_NUMBER)).thenReturn(billStatus("PENDING", null));

        checkBillStatusService.checkBillStatusV2(event, 3);

        verify(billCompletedPublisherPort, never()).publish(any());
        verify(paymentQueueParser, never()).createTransactionEvent(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private BillStatusEntity billStatus(String status, BillStatusEntity.Error error) {
        return BillStatusEntity.builder()
            .operationNumber(OPERATION_NUMBER)
            .status(status)
            .paymentId(PAYMENT_ID)
            .error(error)
            .build();
    }

    private TransactionEvent buildEvent(long ttlTimestamp) {
        TransactionEvent event = new TransactionEvent();

        Header header = new Header();
        header.setTransactionId("TX-1");
        event.setHeader(header);

        Body body = new Body();
        body.setOperationNumber(OPERATION_NUMBER);
        event.setBody(body);

        Map<String, Object> customProperties = new HashMap<>();
        customProperties.put(CheckBillStatusServiceImpl.TTL_TIMESTAMP, ttlTimestamp);
        event.setCustomProperties(customProperties);

        return event;
    }

    private long epochMillisFromNow(long seconds) {
        return Instant.now(Clock.system(LIMA_ZONE)).plusSeconds(seconds).toEpochMilli();
    }
}
