package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import okhttp3.ResponseBody;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.response.TokenOkResponse;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import retrofit2.Response;
import retrofit2.mock.Calls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SecurityRestClientAdapterTest {

    @Mock
    private TokenRestClient tokenRestClient;

    @Mock
    private OpenBankingAccessParameters openBankingAccessParameters;

    @Mock
    private SecurityRestClientParser securityRestClientParser;

    @Mock
    private InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    private SecurityRestClientAdapter securityRestClientAdapter;

    private TokenOkResponse tokenOkResponse;

    private OpenBankingAccess openBankingAccess;

    @BeforeEach
    public void init(){
        this.securityRestClientAdapter = new SecurityRestClientAdapter(
            tokenRestClient,
            openBankingAccessParameters,
            securityRestClientParser,
            interbankRetrofitErrorHandler
        );
        tokenOkResponse = TokenOkResponse.builder().build();
        openBankingAccess = OpenBankingAccess.builder().build();
    }

    @Test
    public void getAccess_Should_Be_Ok() throws Exception {
        // when
        String granType = "client_credentials";
        when(tokenRestClient.getAccess("",granType)).thenReturn(Calls.response(tokenOkResponse));
        when(openBankingAccessParameters.getBasicAuth()).thenReturn("");
        when(openBankingAccessParameters.getGrantType()).thenReturn(granType);
        doNothing().when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(SecurityRestClientAdapter.class));
        when(securityRestClientParser.convertResponseToDomain(tokenOkResponse)).thenReturn(openBankingAccess);
        // then
        OpenBankingAccess resultOpenBankingAccess = securityRestClientAdapter.getAccess();
        // assert
        assertEquals(openBankingAccess,resultOpenBankingAccess );
    }

    @Test
    public void getAccess_Should_Throw_Exception() throws Exception {
        // when
        String granType = "client_credentials";
        when(tokenRestClient.getAccess( "",granType)).thenReturn(Calls.response(Response.error(400,
            ResponseBody.create(null, "Error de comunicacion"))));
        when(openBankingAccessParameters.getBasicAuth()).thenReturn("");
        when(openBankingAccessParameters.getGrantType()).thenReturn(granType);
        doAnswer(invocation -> {
            throw new RestClientException(new java.io.IOException(), SecurityRestClientAdapter.class.getName(), "Error de comunicacion");
        }).when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(SecurityRestClientAdapter.class));
        // then
        assertThrows(RestClientException.class, () -> securityRestClientAdapter.getAccess());
        // assert
    }
}
