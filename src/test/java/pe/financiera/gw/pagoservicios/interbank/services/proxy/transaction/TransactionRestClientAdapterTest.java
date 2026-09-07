package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.InterbankBillStatusResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.util.authorization.InterbankAuthorizationManager;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionRestClientAdapterTest {

    @InjectMocks
    private TransactionRestClientAdapter transactionRestClientAdapter;

    @Mock
    private TransactionRestClientParser transactionRestClientParser;
    @Mock
    private TransactionRestClient transactionRestClient;
    @Mock
    private OpenBankingAccessParameters openBankingAccessParameters;
    @Mock
    private InterbankAuthorizationManager interbankAuthorizationManager;
    @Mock
    private InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    @Test
    public void testGetBillStatus_GivenValidInputs_ShouldReturnBillStatusEntity() throws Exception {
        final String operationNumber = "OPERATION_NUMBER", subscriptionKey = "SUBSCRIPTION_KEY";
        final InterbankBillStatusResponse response = new InterbankBillStatusResponse();
        final Call<InterbankBillStatusResponse> makeCall = mock(Call.class);
        final BillStatusEntity entity = mock(BillStatusEntity.class);
        final OpenBankingAccess openBankingAccess = OpenBankingAccess.builder()
            .accessToken("12345678")
            .build();

        when(openBankingAccessParameters.getSubscriptionKeyOf(any())).thenReturn(subscriptionKey);
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.ofNullable(openBankingAccess));
        when(transactionRestClient.billStatus(eq(subscriptionKey), eq(true), eq("Bearer 12345678"), eq(operationNumber)))
            .thenReturn(makeCall);
        when(makeCall.execute()).thenReturn(Response.success(response));
        doNothing().when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(TransactionRestClientAdapter.class));
        when(transactionRestClientParser.convertToDomain(response)).thenReturn(entity);

        final BillStatusEntity actual = transactionRestClientAdapter.getBillStatus(operationNumber);
        assertEquals(entity, actual);
    }

    @Test
    public void testGetBillStatus_GivenFailResponse_ShouldThrowRestClientException() throws Exception {
        final String operationNumber = "OPERATION_NUMBER", subscriptionKey = "SUBSCRIPTION_KEY";
        final Call<InterbankBillStatusResponse> makeCall = mock(Call.class);
        final BillStatusEntity entity = mock(BillStatusEntity.class);
        final OpenBankingAccess openBankingAccess = OpenBankingAccess.builder()
            .accessToken("12345678")
            .build();

        when(openBankingAccessParameters.getSubscriptionKeyOf(any())).thenReturn(subscriptionKey);
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.ofNullable(openBankingAccess));
        when(transactionRestClient.billStatus(eq(subscriptionKey), eq(true), eq("Bearer 12345678"), eq(operationNumber)))
            .thenReturn(makeCall);
        when(makeCall.execute()).thenReturn(Response.error(400, ResponseBody.create(null, "")));
        doAnswer(invocation -> {
            throw new RestClientException(new IOException(), TransactionRestClientAdapter.class.getName(), "");
        }).when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(TransactionRestClientAdapter.class));

        assertThrows(RestClientException.class, () -> transactionRestClientAdapter.getBillStatus(operationNumber));
    }
}
