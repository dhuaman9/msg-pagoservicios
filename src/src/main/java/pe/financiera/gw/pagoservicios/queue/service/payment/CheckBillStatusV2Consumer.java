package pe.financiera.gw.pagoservicios.queue.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.input.CheckBillStatusService;
import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.framework.pubsub.messaging.exception.NeededRetryException;
import pe.financiera.framework.pubsub.queue.consumer.message.MessageConsumer;

@Slf4j
@Component
public class CheckBillStatusV2Consumer extends BaseConsumer implements MessageConsumer<TransactionEvent> {

    private final CheckBillStatusService checkBillStatusService;

    public CheckBillStatusV2Consumer(final CheckBillStatusService checkBillStatusService) {
        this.checkBillStatusService = checkBillStatusService;
    }

    @Override
    public void accept(final TransactionEvent transactionEvent, final Integer nbRetry) {
        log.info("Event: {} - Retry: {}", transactionEvent, nbRetry);
        try {
            log.info("Processing check bill status. eventId {} and transactionId {}",
                transactionEvent.getHeader().getEventId(), transactionEvent.getHeader().getTransactionId());
            checkBillStatusService.checkBillStatusV2(transactionEvent, nbRetry);
            } catch (NeededRetryException e) {
                log.warn("Check bill status event consumer retry needed: {}, nbRetry: {}", e.getMessage(), nbRetry);
                throw e;
            } catch (Exception e) {
                log.error("Check bill status event consumer error", e);
            }
    }
}
