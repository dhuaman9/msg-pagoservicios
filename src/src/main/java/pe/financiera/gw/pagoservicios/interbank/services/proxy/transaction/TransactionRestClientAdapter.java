package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import org.springframework.stereotype.Service;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.business.output.TransactionPort;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.InterbankBillStatusResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.util.authorization.InterbankAuthorizationManager;
import pe.financiera.gw.pagoservicios.util.constants.OpenBankingConstans;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Service
public class TransactionRestClientAdapter implements TransactionPort {

    private final TransactionRestClientParser transactionRestClientParser;
    private final TransactionRestClient transactionRestClient;
    private final OpenBankingAccessParameters openBankingAccessParameters;
    private final InterbankAuthorizationManager interbankAuthorizationManager;
    private final InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    public static final boolean FORCE_SYNC = true;

    public TransactionRestClientAdapter(final TransactionRestClientParser transactionRestClientParser,
                                        final TransactionRestClient transactionRestClient,
                                        final OpenBankingAccessParameters openBankingAccessParameters,
                                        final InterbankAuthorizationManager interbankAuthorizationManager,
                                        final InterbankRetrofitErrorHandler interbankRetrofitErrorHandler) {
        this.transactionRestClientParser = transactionRestClientParser;
        this.transactionRestClient = transactionRestClient;
        this.openBankingAccessParameters = openBankingAccessParameters;
        this.interbankAuthorizationManager = interbankAuthorizationManager;
        this.interbankRetrofitErrorHandler = interbankRetrofitErrorHandler;
    }

    @Override
    public BillStatusEntity getBillStatus(final String operationNumber) throws IOException, RestClientException, InterbankApiException {
        final String subscriptionKey = openBankingAccessParameters.getSubscriptionKeyOf(OpenBankingConstans.BILLING);
        final Response<InterbankBillStatusResponse> response = transactionRestClient.billStatus(
            subscriptionKey,
            FORCE_SYNC,
            getAuthorization(),
            operationNumber).execute();

        exceptionHandler(response);

        return transactionRestClientParser.convertToDomain(response.body());
    }

    private String getAuthorization() throws RestClientException {
        final Optional<OpenBankingAccess> openBankingAccessOptional = interbankAuthorizationManager.getOpenBankingAccess();
        return openBankingAccessOptional.orElseThrow(() ->
            new RestClientException(new RuntimeException("Missing jwt"), TransactionRestClientAdapter.class.getName(), "Missing jwt")
        ).getAuthorization();
    }

    private void exceptionHandler(Response<?> response) throws IOException, RestClientException, InterbankApiException {
        interbankRetrofitErrorHandler.ensureSuccessful(response, TransactionRestClientAdapter.class);
    }
}
