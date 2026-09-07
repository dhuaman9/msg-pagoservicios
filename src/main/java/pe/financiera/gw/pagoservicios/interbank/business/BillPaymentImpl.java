package pe.financiera.gw.pagoservicios.interbank.business;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillPaymentPort;
import pe.financiera.gw.pagoservicios.interbank.business.output.CheckBillStatusPublisherPort;
import pe.financiera.gw.pagoservicios.interbank.business.input.BillPaymentService;
import pe.financiera.gw.pagoservicios.queue.service.payment.CheckBillStatusRestParser;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

@Slf4j
@Service("billPaymentService")
public class BillPaymentImpl implements BillPaymentService {

    private static final String LOG_PREFIX = "GW_SERVICE_PAY";

    private final BillPaymentPort billPaymentPort;
    private final CheckBillStatusRestParser checkBillStatusRestParser;
    private final CheckBillStatusPublisherPort checkBillStatusPublisherPort;

    @Value("${check.bill.status.timeout}")
    private int checkBillStatusTimeout;
    @Value("${queue.check.bill.status.subscription.max-retries}")
    private int retryFactor;
    @Value("${queue.check.bill.status.subscription.retry-wait-time}")
    private int retryWaitTimeMillis;

    public BillPaymentImpl(final BillPaymentPort billPaymentPort,
                           final CheckBillStatusRestParser checkBillStatusRestParser,
                           final CheckBillStatusPublisherPort checkBillStatusPublisherPort) {
        this.billPaymentPort = billPaymentPort;
        this.checkBillStatusRestParser = checkBillStatusRestParser;
        this.checkBillStatusPublisherPort = checkBillStatusPublisherPort;
    }

    @Override
    public void makePaymentV2(PaymentV2 payment) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_MAKE_PAYMENT_V2_START | correlationId={} | payment={}",
            LOG_PREFIX, payment.getCorrelationId(), payment);

        BillList billList = billPaymentPort.getBillList(payment.getClientId(), payment.getRecipientId(), payment.getServiceId());
        log.info("{}_MAKE_PAYMENT_V2_GET_BILL_LIST_RESPONSE | correlationId={} | billList={}",
            LOG_PREFIX, payment.getCorrelationId(), billList);

        billPaymentPort.makePayment(checkBillStatusRestParser.toPayment(payment));
        log.info("{}_MAKE_PAYMENT_V2_PAYMENT_DONE | correlationId={} | billId={}",
            LOG_PREFIX, payment.getCorrelationId(), payment.getBillId());

        checkBillStatusPublisherPort.publish(
            checkBillStatusRestParser.toTransactionEvent(payment, checkBillStatusTimeout, retryFactor, retryWaitTimeMillis));

        log.info("{}_MAKE_PAYMENT_V2_PUBLISH_COMPLETED | correlationId={}",
            LOG_PREFIX, payment.getCorrelationId());
    }

    @Override
    public BillList getBills(String clientId, String recipientId, String serviceId) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_PRE_CONFIRMATION_STEP_3.2.2_CALLING_IBK_BILLS: recipientId={} serviceId={} clientId={}",
            LOG_PREFIX, recipientId, serviceId, clientId);
        BillList result = billPaymentPort.getBillList(clientId, recipientId, serviceId);
        log.info("{}_PRE_CONFIRMATION_STEP_3.2.4_END_COMPLETED: recipientId={} clientId={}",
            LOG_PREFIX, recipientId, clientId);
        return result;
    }
}
