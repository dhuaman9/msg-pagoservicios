package pe.financiera.gw.pagoservicios.queue.service.payment;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import pe.financiera.framework.event.base.message.third.party.Header;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.Body;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Payment;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.CurrencyTypeEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransferTypeEnum;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static pe.financiera.framework.common.logging.LoggingHeader.*;
import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.TTL_TIMESTAMP;

@Component
public class CheckBillStatusRestParser {

    private final Clock clock;

    public CheckBillStatusRestParser(final Clock clock) {
        this.clock = clock;
    }

    public Payment toPayment(PaymentV2 payment) {
        return Payment.builder()
            .recipientId(payment.getRecipientId())
            .serviceId(payment.getServiceId())
            .billId(payment.getBillId())
            .correlationId(payment.getCorrelationId())
            .clientId(payment.getClientId())
            .build();
    }

    public TransactionEvent toTransactionEvent(PaymentV2 payment, Integer checkBillStatusTimeout, Integer retryFactor, Integer retryWaitTimeMillis) {
        final TransactionEvent transactionEvent = new TransactionEvent();
        transactionEvent.setHeader(toHeader(payment, retryFactor, retryWaitTimeMillis));
        transactionEvent.setBody(toBody(payment));

//        Map<String, Object> customProperties = payment.getCustomProperties();
//        customProperties.put(TTL_TIMESTAMP, Instant.now(clock).plusSeconds(checkBillStatusTimeout).toEpochMilli());
//        transactionEvent.setCustomProperties(customProperties);

        /*
        Que toTransactionEvent(...) nunca reviente por un NullPointerException si payment.getCustomProperties() viene vacío (null),
        y que siempre pueda agregarle   la fecha de vencimiento (TTL_TIMESTAMP) al mensaje antes de publicarlo —
        sin importar si el payment original traía ese mapa completo, vacío, o ausente.
        * */
        Map<String, Object> customProperties = Optional.ofNullable(payment.getCustomProperties())
            .map(HashMap::new)
            .orElseGet(HashMap::new);
        customProperties.put(TTL_TIMESTAMP, Instant.now(clock).plusSeconds(checkBillStatusTimeout).toEpochMilli());
        transactionEvent.setCustomProperties(customProperties);

        return transactionEvent;
    }


    private Header toHeader(final PaymentV2 payment, Integer retryFactor, Integer retryWaitTimeMillis) {
        final Header header = new Header();
        header.setCommandTrigger(payment.getCommandTrigger());
        header.setEventId(UUID.randomUUID().toString());
        header.setEventTag(payment.getEventTag());
        header.setTransactionId(MDC.get(TRANSACTION_ID.getLogKey()));
        header.setUserId(MDC.get(USER_ID.getLogKey()));
        header.setRoleCode(MDC.get(ROLE_CODE.getLogKey()));
        header.setApplication(MDC.get(APPLICATION.getLogKey()));
        header.setPlatform(MDC.get(PLATFORM.getLogKey()));
        header.setDeviceId(MDC.get(DEVICE_ID.getLogKey()));
        header.setTimestamp(Date.from(Instant.now(clock)));
        header.setRetryId(UUID.randomUUID().toString());
        header.setRetryFactor(retryFactor);
        header.setRetryWaitTimeMillis(retryWaitTimeMillis);

        return header;
    }

    private Body toBody(final PaymentV2 payment) {
        final Body body = new Body();
        body.setOperationId(payment.getOperationId());
        body.setOperationNumber(payment.getOperationNumber());
        body.setTransactionId(payment.getOperationNumber());
        body.setContract(payment.getExternalAccountId());
        body.setCurrencyType(CurrencyTypeEnum.PEN.name());
        body.setTransferAmount(payment.getAmount());
        body.setTransferType(TransferTypeEnum.CASH_OUT.name());
        body.setUserId(payment.getUserId());
        body.setDocumentType(payment.getDocumentType());
        body.setDocumentNumber(payment.getDocumentNumber());
        body.setCreationDate(payment.getCreationDate());
        body.setTokenTunki(payment.getTokenTunki());
        body.setReferenceNumber(payment.getOperationNumber());
        return body;
    }

}
