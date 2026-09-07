package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.business.output.SecurityRepositoryPort;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.response.TokenOkResponse;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import retrofit2.Response;

import java.io.IOException;

@Service
@Slf4j
public class SecurityRestClientAdapter implements SecurityRepositoryPort {

    private TokenRestClient tokenRestClient;

    private OpenBankingAccessParameters openBankingAccessParameters;

    private SecurityRestClientParser securityRestClientParser;

    private InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    @Autowired
    public SecurityRestClientAdapter(
        TokenRestClient tokenRestClient,
        OpenBankingAccessParameters openBankingAccessParameters,
        SecurityRestClientParser securityRestClientParser,
        InterbankRetrofitErrorHandler interbankRetrofitErrorHandler
    ) {
        this.tokenRestClient = tokenRestClient;
        this.openBankingAccessParameters = openBankingAccessParameters;
        this.securityRestClientParser = securityRestClientParser;
        this.interbankRetrofitErrorHandler = interbankRetrofitErrorHandler;
    }

    @Override
    public OpenBankingAccess getAccess() throws IOException, RestClientException, InterbankApiException {
        Response<TokenOkResponse> response = tokenRestClient.getAccess(
            openBankingAccessParameters.getBasicAuth(),
            openBankingAccessParameters.getGrantType()).execute();
        this.exceptionHandler(response);
        return securityRestClientParser.convertResponseToDomain(response.body());
    }

    private void exceptionHandler(Response<?> response) throws IOException, RestClientException, InterbankApiException {
        interbankRetrofitErrorHandler.ensureSuccessful(response, SecurityRestClientAdapter.class);
    }
}
