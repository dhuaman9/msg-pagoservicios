package pe.financiera.gw.pagoservicios.interbank.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatus;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillCompletedPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.business.output.TransactionPort;
import pe.financiera.gw.pagoservicios.interbank.business.input.CheckBillStatusService;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.IncorporateEventTagEnum;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.queue.service.payment.PaymentQueueParser;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import pe.financiera.framework.pubsub.messaging.exception.NeededRetryException;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static pe.financiera.gw.pagoservicios.util.constants.ApplicationConstants.ZONE_ID_LIMA;

@Service
@Slf4j
public class CheckBillStatusServiceImpl implements CheckBillStatusService {

    private final TransactionPort transactionPort;
    private final PaymentQueueParser serviceParser;
    private final BillCompletedPublisherPort billCompletedPublisherPort;
    private final PaymentQueueParser paymentQueueParser;
    private final Clock clock;

    @Value("${queue.sp.check.bill.status.v2.subscription.max-retries}")
    private Integer maxRetries;

    static final String TTL_TIMESTAMP = "ttl_timestamp";

    public CheckBillStatusServiceImpl(final TransactionPort transactionPort,
                                      final PaymentQueueParser serviceParser,
                                      final BillCompletedPublisherPort billCompletedPublisherPort,
                                      final PaymentQueueParser paymentQueueParser,
                                      final Clock clock) {
        this.transactionPort = transactionPort;
        this.serviceParser = serviceParser;
        this.billCompletedPublisherPort = billCompletedPublisherPort;
        this.paymentQueueParser = paymentQueueParser;
        this.clock = clock;
    }

    @Override
    public void checkBillStatusV2(final TransactionEvent event, Integer nbRetry) throws IOException, RestClientException, InterbankApiException {
        Long eventTTL = ((Long) event.getCustomProperties().get(TTL_TIMESTAMP));
        isOnTimeToProcess(eventTTL, event.getHeader().getTransactionId());
        final BillStatusEntity billStatus = transactionPort.getBillStatus(event.getBody().getOperationNumber());
        log.info("service Interbank getBillStats: {}", billStatus);
        processBillV2(billStatus, event, nbRetry);
    }

    private void isOnTimeToProcess(final Long eventTtl, String transactionId) {
        final long now = Instant.now(clock).atZone(ZoneId.of(ZONE_ID_LIMA)).toInstant().toEpochMilli();

        if ((now < eventTtl)) {
            log.info("service Interbank operation not ready to be checked for event id: {}", transactionId);
            throw new NeededRetryException();
        }
    }

    private void processBillV2(final BillStatusEntity billStatus, final TransactionEvent event, final Integer nbRetry) {
        log.info("processBillV2: billStatus: {}, retry: {}", billStatus.toString(), nbRetry);
        if (BillStatus.SETTLED.name().equals(billStatus.getStatus())) {
            publishSuccessMessage(billStatus, event);
        } else if (BillStatus.FAILED.name().equals(billStatus.getStatus())) {
            publishFailedMessage(event, billStatus);
        } else {
            validateNeedToRetry(nbRetry);
        }
    }

    private void validateNeedToRetry(int nbRetry) {
        if (nbRetry < maxRetries) {
            throw new NeededRetryException();
        }
    }

    private void publishSuccessMessage(final BillStatusEntity billStatus, final TransactionEvent event) {
        log.info("Publishing successful event: {}", serviceParser.updateTransactionEvent(event, billStatus.getPaymentId()));
        billCompletedPublisherPort.publish(event);
    }

    private void publishFailedMessage(TransactionEvent event, BillStatusEntity billStatus) {
        log.info("Publishing failed event: {} {}", event, billStatus);
        billCompletedPublisherPort.publish(paymentQueueParser.createTransactionEvent(
            IncorporateEventTagEnum.FAILED.name(),
            billStatus.getPaymentId(),
            billStatus.getOperationNumber(),
            billStatus.getError().getMessage(),
            billStatus.getError().getCode()));
    }
}
