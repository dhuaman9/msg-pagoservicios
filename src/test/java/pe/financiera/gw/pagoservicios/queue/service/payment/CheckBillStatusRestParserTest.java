package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import pe.financiera.gw.pagoservicios.interbank.business.domain.DirectPayment;
import pe.financiera.gw.pagoservicios.interbank.business.domain.DirectPaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Payment;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.CurrencyTypeEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransferTypeEnum;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pe.financiera.framework.common.logging.LoggingHeader.TRANSACTION_ID;
import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.LIMA_ZONE;
import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.TTL_TIMESTAMP;

class CheckBillStatusRestParserTest {

    private CheckBillStatusRestParser parser;

    private PaymentV2 paymentV2;
    private DirectPaymentV2 directPaymentV2;

    @BeforeEach
    void setUp() {
        parser = new CheckBillStatusRestParser(Clock.system(LIMA_ZONE));
        paymentV2 = PaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .billId("00000005")
            .correlationId("123456")
            .clientId("987123456")
            .operationId("OP-ID")
            .operationNumber("OP-NUMBER")
            .externalAccountId("ACC-1")
            .amount(new BigDecimal("91.42"))
            .userId("USER-1")
            .documentType("DNI")
            .documentNumber("12345678")
            .creationDate("2026-08-12T10:00:00Z")
            .tokenTunki("TOKEN")
            .commandTrigger("SERVICE_PAYMENT")
            .eventTag("SERVICE_PAYMENT_IN_PROGRESS")
            .customProperties(new HashMap<>())
            .build();

        directPaymentV2 = DirectPaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .correlationId("123456")
            .clientId("987123456")
            .operationId("OP-ID")
            .operationNumber("OP-NUMBER")
            .externalAccountId("ACC-1")
            .amount(new BigDecimal("91.42"))
            .userId("USER-1")
            .documentType("DNI")
            .documentNumber("12345678")
            .creationDate("2026-08-12T10:00:00Z")
            .tokenTunki("TOKEN")
            .commandTrigger("SERVICE_PAYMENT")
            .eventTag("SERVICE_PAYMENT_IN_PROGRESS")
            .currencyType("PEN")
            .customProperties(new HashMap<>())
            .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void toPayment_shouldMapIdentifiers() {
        Payment payment = parser.toPayment(paymentV2);

        assertEquals("01006", payment.getRecipientId());
        assertEquals("01", payment.getServiceId());
        assertEquals("00000005", payment.getBillId());
        assertEquals("123456", payment.getCorrelationId());
        assertEquals("987123456", payment.getClientId());
    }

    @Test
    void toTransactionEvent_shouldBuildHeaderBodyAndTtl() {
        MDC.put(TRANSACTION_ID.getLogKey(), "TX-1");
        long before = System.currentTimeMillis();

        TransactionEvent event = parser.toTransactionEvent(paymentV2, 120, 3, 180);

        assertNotNull(event.getHeader().getEventId());
        assertNotNull(event.getHeader().getRetryId());
        assertEquals("TX-1", event.getHeader().getTransactionId());
        assertEquals("SERVICE_PAYMENT", event.getHeader().getCommandTrigger());
        assertEquals("SERVICE_PAYMENT_IN_PROGRESS", event.getHeader().getEventTag());
        assertEquals(3, event.getHeader().getRetryFactor());
        assertEquals(180, event.getHeader().getRetryWaitTimeMillis());

        assertEquals("OP-ID", event.getBody().getOperationId());
        assertEquals("OP-NUMBER", event.getBody().getOperationNumber());
        assertEquals("OP-NUMBER", event.getBody().getReferenceNumber());
        assertEquals("ACC-1", event.getBody().getContract());
        assertEquals(CurrencyTypeEnum.PEN.name(), event.getBody().getCurrencyType());
        assertEquals(TransferTypeEnum.CASH_OUT.name(), event.getBody().getTransferType());
        assertEquals(new BigDecimal("91.42"), event.getBody().getTransferAmount());
        assertEquals("USER-1", event.getBody().getUserId());
        assertEquals("DNI", event.getBody().getDocumentType());
        assertEquals("12345678", event.getBody().getDocumentNumber());

        long ttl = (Long) event.getCustomProperties().get(TTL_TIMESTAMP);
        assertTrue(ttl >= before + 120_000L);
    }

   @Test
    void toTransactionEvent_shouldNotThrowWhenCustomPropertiesIsNull() {
        PaymentV2 paymentSinCustomProperties = PaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .billId("00000005")
            .correlationId("123456")
            .clientId("987123456")
            .operationId("OP-ID")
            .operationNumber("OP-NUMBER")
            .externalAccountId("ACC-1")
            .amount(new BigDecimal("91.42"))
            .userId("USER-1")
            .documentType("DNI")
            .documentNumber("12345678")
            .creationDate("2026-08-12T10:00:00Z")
            .tokenTunki("TOKEN")
            .commandTrigger("SERVICE_PAYMENT")
            .eventTag("SERVICE_PAYMENT_IN_PROGRESS")
            .customProperties(null)
            .build();

        TransactionEvent event = parser.toTransactionEvent(paymentSinCustomProperties, 120, 3, 180);

        assertNotNull(event.getCustomProperties());
        assertTrue(event.getCustomProperties().containsKey(TTL_TIMESTAMP));
    }

    @Test
    void toDirectPayment_shouldMapFieldsAndValidateCurrency() {
        DirectPayment result = parser.toDirectPayment(directPaymentV2);

        assertEquals("01006", result.getRecipientId());
        assertEquals("01", result.getServiceId());
        assertEquals("123456", result.getCorrelationId());
        assertEquals("987123456", result.getClientId());
        assertEquals(CurrencyTypeEnum.PEN.name(), result.getCurrency());
        assertEquals("91.42", result.getAmount());
    }

    @Test
    void toDirectPayment_shouldThrowWhenCurrencyTypeIsNull() {
        DirectPaymentV2 sinCurrencyType = DirectPaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .correlationId("123456")
            .clientId("987123456")
            .currencyType(null)
            .amount(new BigDecimal("50.00"))
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> parser.toDirectPayment(sinCurrencyType));
    }

    @Test
    void toDirectPayment_shouldThrowWhenCurrencyTypeIsInvalid() {
        DirectPaymentV2 currencyTypeInvalido = DirectPaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .correlationId("123456")
            .clientId("987123456")
            .currencyType("XYZ")
            .amount(new BigDecimal("50.00"))
            .build();

        assertThrows(IllegalArgumentException.class,
            () -> parser.toDirectPayment(currencyTypeInvalido));
    }

    @Test
    void toDirectTransactionEvent_shouldBuildHeaderBodyAndTtl() {
        MDC.put(TRANSACTION_ID.getLogKey(), "TX-1");
        long before = System.currentTimeMillis();

        TransactionEvent event = parser.toDirectTransactionEvent(directPaymentV2, 120, 3, 180);

        assertNotNull(event.getHeader().getEventId());
        assertNotNull(event.getHeader().getRetryId());
        assertEquals("TX-1", event.getHeader().getTransactionId());
        assertEquals("SERVICE_PAYMENT", event.getHeader().getCommandTrigger());
        assertEquals("SERVICE_PAYMENT_IN_PROGRESS", event.getHeader().getEventTag());
        assertEquals(3, event.getHeader().getRetryFactor());
        assertEquals(180, event.getHeader().getRetryWaitTimeMillis());

        assertEquals("OP-ID", event.getBody().getOperationId());
        assertEquals("OP-NUMBER", event.getBody().getOperationNumber());
        assertEquals("OP-NUMBER", event.getBody().getReferenceNumber());
        assertEquals("ACC-1", event.getBody().getContract());
        assertEquals(CurrencyTypeEnum.PEN.name(), event.getBody().getCurrencyType());
        assertEquals(TransferTypeEnum.CASH_OUT.name(), event.getBody().getTransferType());
        assertEquals(new BigDecimal("91.42"), event.getBody().getTransferAmount());
        assertEquals("USER-1", event.getBody().getUserId());
        assertEquals("DNI", event.getBody().getDocumentType());
        assertEquals("12345678", event.getBody().getDocumentNumber());

        long ttl = (Long) event.getCustomProperties().get(TTL_TIMESTAMP);
        assertTrue(ttl >= before + 120_000L);
    }

    @Test
    void toDirectTransactionEvent_shouldNotThrowWhenCustomPropertiesIsNull() {
        DirectPaymentV2 sinCustomProperties = DirectPaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .correlationId("123456")
            .clientId("987123456")
            .operationId("OP-ID")
            .operationNumber("OP-NUMBER")
            .externalAccountId("ACC-1")
            .amount(new BigDecimal("91.42"))
            .userId("USER-1")
            .documentType("DNI")
            .documentNumber("12345678")
            .creationDate("2026-08-12T10:00:00Z")
            .tokenTunki("TOKEN")
            .commandTrigger("SERVICE_PAYMENT")
            .eventTag("SERVICE_PAYMENT_IN_PROGRESS")
            .currencyType("PEN")
            .customProperties(null)
            .build();

        TransactionEvent event = parser.toDirectTransactionEvent(sinCustomProperties, 120, 3, 180);

        assertNotNull(event.getCustomProperties());
        assertTrue(event.getCustomProperties().containsKey(TTL_TIMESTAMP));
    }
}
