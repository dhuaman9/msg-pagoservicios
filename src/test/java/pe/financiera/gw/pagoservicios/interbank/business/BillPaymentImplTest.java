package pe.financiera.gw.pagoservicios.interbank.business;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Bill;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Payment;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillPaymentPort;
import pe.financiera.gw.pagoservicios.interbank.business.output.CheckBillStatusPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.queue.service.payment.CheckBillStatusRestParser;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import pe.financiera.gw.pagoservicios.interbank.business.domain.DirectPayment;
import pe.financiera.gw.pagoservicios.interbank.business.domain.DirectPaymentV2;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillPaymentImplTest {

    @Mock
    private BillPaymentPort billPaymentPort;

    @Mock
    private CheckBillStatusRestParser checkBillStatusRestParser;

    @Mock
    private CheckBillStatusPublisherPort checkBillStatusPublisherPort;

    private BillPaymentImpl billPaymentImpl;

    private PaymentV2 paymentV2;

    private DirectPaymentV2 directPaymentV2;


    @BeforeEach
    void setUp() {
        billPaymentImpl = new BillPaymentImpl(billPaymentPort, checkBillStatusRestParser, checkBillStatusPublisherPort);
        ReflectionTestUtils.setField(billPaymentImpl, "checkBillStatusTimeout", 120);
        ReflectionTestUtils.setField(billPaymentImpl, "retryFactor", 3);
        ReflectionTestUtils.setField(billPaymentImpl, "retryWaitTimeMillis", 180);

        paymentV2 = PaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .billId("00000005")
            .correlationId("123456")
            .clientId("987123456")
            .build();

        directPaymentV2 = DirectPaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .correlationId("123456")
            .clientId("987123456")
            .build();

    }

    @Test
    void makePaymentV2_shouldValidateBillsPayAndPublishCheckStatus() throws Exception {
        Payment payment = Payment.builder().billId("00000005").build();
        TransactionEvent event = new TransactionEvent();
        when(checkBillStatusRestParser.toPayment(paymentV2)).thenReturn(payment);
        when(checkBillStatusRestParser.toTransactionEvent(paymentV2, 120, 3, 180)).thenReturn(event);

        billPaymentImpl.makePaymentV2(paymentV2);

        verify(billPaymentPort, times(1)).getBillList("987123456", "01006", "01");
        verify(billPaymentPort, times(1)).makePayment(payment);
        verify(checkBillStatusPublisherPort, times(1)).publish(event);
    }

    @Test
    void makePaymentV2_shouldPropagateInterbankApiExceptionWithoutPublishing() throws Exception {
        InterbankApiException expected = new InterbankApiException(
            "07.01.03", "BillNotFoundException", HttpStatus.NOT_FOUND);
        when(billPaymentPort.getBillList(anyString(), anyString(), anyString())).thenThrow(expected);

        InterbankApiException thrown = assertThrows(InterbankApiException.class,
            () -> billPaymentImpl.makePaymentV2(paymentV2));

        assertSame(expected, thrown);
        verify(billPaymentPort, never()).makePayment(any());
        verify(checkBillStatusPublisherPort, never()).publish(any());
    }

    @Test
    void makePaymentV2_shouldPropagatePublisherFailure() throws Exception {
        Payment payment = Payment.builder().billId("00000005").build();
        TransactionEvent event = new TransactionEvent();
        when(checkBillStatusRestParser.toPayment(paymentV2)).thenReturn(payment);
        when(checkBillStatusRestParser.toTransactionEvent(any(), anyInt(), anyInt(), anyInt())).thenReturn(event);
        doThrow(new IllegalStateException("pubsub down")).when(checkBillStatusPublisherPort).publish(event);

        assertThrows(IllegalStateException.class, () -> billPaymentImpl.makePaymentV2(paymentV2));

        verify(billPaymentPort, times(1)).makePayment(payment);
    }

    @Test
    void getBills_shouldDelegateToPort() throws Exception {
        BillList billList = BillList.builder()
            .client(BillList.Client.builder().id("A1580040").build())
            .bills(Collections.singletonList(Bill.builder().id("00000003").build()))
            .build();
        when(billPaymentPort.getBillList("987123456", "01006", "01")).thenReturn(billList);

        BillList result = billPaymentImpl.getBills("987123456", "01006", "01");

        assertSame(billList, result);
        assertEquals("A1580040", result.getClient().getId());
        verify(billPaymentPort, times(1)).getBillList("987123456", "01006", "01");
    }

    @Test
    void makeDirectPaymentV2_shouldMakeDirectPaymentAndPublishCheckStatus() throws Exception {
        DirectPayment directPayment = DirectPayment.builder().clientId("987123456").build();
        TransactionEvent event = new TransactionEvent();
        when(checkBillStatusRestParser.toDirectPayment(directPaymentV2)).thenReturn(directPayment);
        when(checkBillStatusRestParser.toDirectTransactionEvent(directPaymentV2, 120, 3, 180)).thenReturn(event);

        billPaymentImpl.makeDirectPaymentV2(directPaymentV2);

        verify(billPaymentPort, times(1)).makeDirectPayment(directPayment);
        verify(checkBillStatusPublisherPort, times(1)).publish(event);

    }

    @Test
    void makeDirectPaymentV2_shouldPropagateInterbankApiExceptionWithoutPublishing() throws Exception {
        InterbankApiException expected = new InterbankApiException(
            "07.01.03", "DirectPaymentFailed", HttpStatus.BAD_GATEWAY);
        when(checkBillStatusRestParser.toDirectPayment(directPaymentV2))
            .thenReturn(DirectPayment.builder().build());
        doThrow(expected).when(billPaymentPort).makeDirectPayment(any());

        InterbankApiException thrown = assertThrows(InterbankApiException.class,
            () -> billPaymentImpl.makeDirectPaymentV2(directPaymentV2));

        assertSame(expected, thrown);
        verify(checkBillStatusPublisherPort, never()).publish(any());
    }

    @Test
    void makeDirectPaymentV2_shouldPropagateUnexpectedExceptionWithoutWrapping() throws Exception {
        DirectPayment directPayment = DirectPayment.builder().build();
        TransactionEvent event = new TransactionEvent();
        when(checkBillStatusRestParser.toDirectPayment(directPaymentV2)).thenReturn(directPayment);
        when(checkBillStatusRestParser.toDirectTransactionEvent(any(), anyInt(), anyInt(), anyInt())).thenReturn(event);
        doThrow(new IllegalStateException("pubsub down")).when(checkBillStatusPublisherPort).publish(event);

        assertThrows(IllegalStateException.class, () -> billPaymentImpl.makeDirectPaymentV2(directPaymentV2));

        verify(billPaymentPort, times(1)).makeDirectPayment(directPayment);
    }


}
