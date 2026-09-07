package pe.financiera.gw.pagoservicios.queue.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pe.financiera.framework.event.base.message.third.party.Error;
import pe.financiera.framework.event.base.message.third.party.Header;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.Body;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.CommandTriggerEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentQueueParser {

    private final Clock clock;

    public TransactionEvent createTransactionEvent(final String eventTag, final String paymentId,
                                                   final String operationNumber, final String errorMessage, final String errorCode) {
        final TransactionEvent event = new TransactionEvent();

        event.setHeader(getHeader(eventTag));
        event.setBody(getBody(operationNumber));
        event.setCustomProperties(getCustomProperties(paymentId));
        event.setError(getError(errorMessage, errorCode));

        return event;
    }

    public TransactionEvent updateTransactionEvent(final TransactionEvent event, final String paymentId) {
        event.getCustomProperties().put("paymentId", paymentId);
        event.getHeader().setTimestamp(Date.from(Instant.now(clock)));
        event.getBody().setResult(true);
        return event;
    }

    private Header getHeader(String eventTag) {
        final Header header = new Header();
        header.setEventId(UUID.randomUUID().toString());
        header.setCommandTrigger(CommandTriggerEnum.SERVICE_PAYMENT.name());
        header.setEventTag(eventTag);
        header.setTimestamp(Date.from(Instant.now(clock)));
        return header;
    }

    private Body getBody(String operationNumber) {
        final Body body = new Body();
        body.setOperationNumber(operationNumber);
        body.setResult(false);
        body.setCreationDate(OffsetDateTime.now(clock).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return body;
    }

    private Map<String, Object> getCustomProperties(String paymentId) {
        final Map<String, Object> customProperties = new HashMap<>();
        customProperties.put("paymentId", paymentId);
        return customProperties;
    }

    private Error getError(final String errorMessage, final String errorCode) {
        if (StringUtils.isEmpty(errorMessage) && StringUtils.isEmpty(errorCode))
            return null;

        final Error eventError = new Error();
        eventError.setDescription(errorMessage);
        eventError.setCode(errorCode);
        return eventError;
    }
}
