package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Payment;
import pe.financiera.gw.pagoservicios.interbank.business.output.BillPaymentPort;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.GetBillResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.util.authorization.InterbankAuthorizationManager;
import pe.financiera.gw.pagoservicios.util.constants.OpenBankingConstans;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class BillPaymentRestClientAdapter implements BillPaymentPort {

    private static final String LOG_PREFIX = "GW_SERVICE_PAY";

    private final BillPaymentRestClient billPaymentRestClient;
    private final OpenBankingAccessParameters openBankingAccessParameters;
    private final InterbankAuthorizationManager interbankAuthorizationManager;
    private final BillPaymentRestParser billPaymentRestParser;
    private final InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    private static final String APPLICATION = OpenBankingConstans.BILLING;

    public BillPaymentRestClientAdapter(final BillPaymentRestClient billPaymentRestClient,
                                        final OpenBankingAccessParameters openBankingAccessParameters,
                                        final InterbankAuthorizationManager interbankAuthorizationManager,
                                        final BillPaymentRestParser billPaymentRestParser,
                                        final InterbankRetrofitErrorHandler interbankRetrofitErrorHandler) {
        this.billPaymentRestClient = billPaymentRestClient;
        this.openBankingAccessParameters = openBankingAccessParameters;
        this.interbankAuthorizationManager = interbankAuthorizationManager;
        this.billPaymentRestParser = billPaymentRestParser;
        this.interbankRetrofitErrorHandler = interbankRetrofitErrorHandler;
    }

    @Override
    public void makePayment(Payment payment) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_CONFIRMATION_STEP_4.9.3_CALLING_IBK_PAY_BILLING: correlationId={} recipientId={} serviceId={} billId={}",
            LOG_PREFIX, payment.getCorrelationId(), payment.getRecipientId(), payment.getServiceId(), payment.getBillId());
        final String subscriptionKey = openBankingAccessParameters.getSubscriptionKeyOf(APPLICATION);
        Optional<OpenBankingAccess> openBankingAccessOptional = interbankAuthorizationManager.getOpenBankingAccess();
        if (!openBankingAccessOptional.isPresent()) {
            throw new RestClientException(
                new RuntimeException("Missing jwt"),
                BillPaymentRestClientAdapter.class.getName(),
                "Missing jwt"
            );
        }
        OpenBankingAccess openBankingAccess = openBankingAccessOptional.get();
        Response<Void> response = billPaymentRestClient.payBilling(
            subscriptionKey,
            payment.getCorrelationId(),
            false,
            openBankingAccess.getAuthorization(),
            payment.getRecipientId(),
            payment.getServiceId(),
            payment.getBillId()
        ).execute();
        interbankRetrofitErrorHandler.ensureSuccessful(response, BillPaymentRestClientAdapter.class);
    }

//    @Override
//    public List<Bill> getBills(String clientId, String recipientId, String serviceId) throws IOException, RestClientException, InterbankApiException {
//        log.info("{}_PRE_CONFIRMATION_STEP_3.2.3_CALLING_IBK_API: recipientId={} serviceId={} clientId={}",
//            LOG_PREFIX, recipientId, serviceId, clientId);
//        final String subscriptionKey = openBankingAccessParameters.getSubscriptionKeyOf(APPLICATION);
//        OpenBankingAccess openBankingAccess = interbankAuthorizationManager.getOpenBankingAccess().get();
//        Response<GetBillResponse> response = billPaymentRestClient.getBills(
//            subscriptionKey,
//            openBankingAccess.getAuthorization(),
//            recipientId,
//            serviceId,
//            clientId
//        ).execute();
//        if (!response.isSuccessful()) {
//            log.error("{}_PRE_CONFIRMATION_STEP_3.2.3_ERROR_IBK_API: recipientId={} code={}", LOG_PREFIX, recipientId, response.code());
//        }
//        interbankRetrofitErrorHandler.ensureSuccessful(response, BillPaymentRestClientAdapter.class);
//        return billPaymentRestParser.getBillResponseToDomain(response.body());
//    }

    @Override
    public BillList getBillList(String clientId, String recipientId, String serviceId) throws IOException, RestClientException, InterbankApiException {
        log.info("{}_PRE_CONFIRMATION_STEP_3.2.3_CALLING_IBK_API: recipientId={} serviceId={} clientId={}",
            LOG_PREFIX, recipientId, serviceId, clientId);
        final String subscriptionKey = openBankingAccessParameters.getSubscriptionKeyOf(APPLICATION);
        //OpenBankingAccess openBankingAccess = interbankAuthorizationManager.getOpenBankingAccess().get();
        //si interbankAuthorizationManager.getOpenBankingAccess() devuelve el Optional vacío
        //(porque Interbank falló al renovar el token ), ese .get() devolvera : NoSuchElementException.
        OpenBankingAccess openBankingAccess = interbankAuthorizationManager.getOpenBankingAccess()
            .orElseThrow(() -> new RestClientException(
                new RuntimeException("Missing jwt"),
                BillPaymentRestClientAdapter.class.getName(),
                "Missing jwt"
            ));

        Response<GetBillResponse> response = billPaymentRestClient.getBills(
            subscriptionKey,
            openBankingAccess.getAuthorization(),
            recipientId,
            serviceId,
            clientId
        ).execute();
        if (!response.isSuccessful()) {
            log.error("{}_PRE_CONFIRMATION_STEP_3.2.3_ERROR_IBK_API: recipientId={} code={}", LOG_PREFIX, recipientId, response.code());
        }
        interbankRetrofitErrorHandler.ensureSuccessful(response, BillPaymentRestClientAdapter.class);
        return billPaymentRestParser.toBillList(response.body());
    }
}
