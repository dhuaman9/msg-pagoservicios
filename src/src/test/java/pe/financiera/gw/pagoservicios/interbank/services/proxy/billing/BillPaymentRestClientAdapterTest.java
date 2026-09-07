package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.config.interbank.OpenBankingAccessParameters;
import pe.financiera.gw.pagoservicios.interbank.business.domain.*;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.BillResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.ClientResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.GetBillResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.error.InterbankRetrofitErrorHandler;
import pe.financiera.gw.pagoservicios.util.authorization.InterbankAuthorizationManager;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import retrofit2.Response;
import retrofit2.mock.Calls;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pe.financiera.gw.pagoservicios.util.constants.OpenBankingConstans.BILLING;

@ExtendWith(MockitoExtension.class)
public class BillPaymentRestClientAdapterTest {

    @Mock
    private BillPaymentRestClient billPaymentRestClient;

    @Mock
    private OpenBankingAccessParameters openBankingAccessParameters;

    @Mock
    private InterbankAuthorizationManager interbankAuthorizationManager;

    @Mock
    private BillPaymentRestParser billPaymentRestParser;

    @Mock
    private InterbankRetrofitErrorHandler interbankRetrofitErrorHandler;

    private BillPaymentRestClientAdapter billPaymentRestClientAdapter;

    private Payment payment;

    private OpenBankingAccess access;

    private List<Bill> billList;

    private List<BillResponse> billResponsesList;

    private GetBillResponse getBillResponse;

    private String subscriptionKey = "subscriptionKey";


    @BeforeEach
    public void init() {
        billPaymentRestClientAdapter = new BillPaymentRestClientAdapter(
            billPaymentRestClient,
            openBankingAccessParameters,
            interbankAuthorizationManager,
            billPaymentRestParser,
            interbankRetrofitErrorHandler
        );
        payment = Payment.builder()
            .recipientId("01006")
            .serviceId("01")
            .billId("00000005")
            .correlationId("123456")
            .build();

        access = OpenBankingAccess.builder()
            .accessToken("token")
            .build();

        billList = new ArrayList<>();
        billResponsesList = new ArrayList<>();
        getBillResponse = new GetBillResponse();
        getBillResponse.setBills(billResponsesList);
    }

//    @Test
//    public void getBills() throws Exception {
//        doNothing().when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(BillPaymentRestClientAdapter.class));
//
//        ClientResponse clientResponse = new ClientResponse();
//        clientResponse.setId("A1580040");
//        BillResponse billObj = new BillResponse();
//        billObj.setCurrency("PEN");
//        billObj.setTotalAmount("91.42");
//        billObj.setDiscount("91.42");
//        billObj.setFeeAmount("0.00");
//        billObj.setCommission("0.00");
//        billObj.setDueDate("2018-08-18");
//        billObj.setId("00000003");
//
//        billResponsesList.add(billObj);
//        billList.add(Bill.builder()
//            .clientId(clientResponse.getId())
//            .currency(billObj.getCurrency())
//            .totalAmount(billObj.getTotalAmount())
//            .discount(billObj.getDiscount())
//            .feeAmount(billObj.getFeeAmount())
//            .commission(billObj.getCommission())
//            .dueDate(billObj.getDueDate())
//            .id(billObj.getId())
//            .build()
//        );
//
//        GetBillResponse getBillResponse = new GetBillResponse();
//        getBillResponse.setBills(billResponsesList);
//        getBillResponse.setClient(clientResponse);
//
//        when(openBankingAccessParameters.getSubscriptionKeyOf(BILLING)).thenReturn(subscriptionKey);
//        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.of(access));
//        when(billPaymentRestParser.getBillResponseToDomain(getBillResponse)).thenReturn(billList);
//        when(billPaymentRestClient.getBills(any(), any(), any(), any(), any())).thenReturn(Calls.response(getBillResponse));
//        List<Bill> expectedBillList = billPaymentRestClientAdapter.getBills(
//            payment.getClientId(), payment.getRecipientId(), payment.getServiceId());
//        assertEquals(expectedBillList.get(0).getClientId(), billList.get(0).getClientId());
//    }

    @Test
    public void getBillList() throws Exception {
        doNothing().when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(BillPaymentRestClientAdapter.class));

        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setId("A1580040");
        BillResponse billObj = new BillResponse();
        billObj.setCurrency("PEN");
        billObj.setTotalAmount("91.42");
        billObj.setDiscount("91.42");
        billObj.setFeeAmount("0.00");
        billObj.setCommission("0.00");
        billObj.setDueDate("2018-08-18");
        billObj.setId("00000003");

        billResponsesList.add(billObj);
        billList.add(Bill.builder()
            .clientId(clientResponse.getId())
            .currency(billObj.getCurrency())
            .totalAmount(billObj.getTotalAmount())
            .discount(billObj.getDiscount())
            .feeAmount(billObj.getFeeAmount())
            .commission(billObj.getCommission())
            .dueDate(billObj.getDueDate())
            .id(billObj.getId())
            .build()
        );

        GetBillResponse getBillResponse = new GetBillResponse();
        getBillResponse.setBills(billResponsesList);
        getBillResponse.setClient(clientResponse);

        BillList expectedResult = BillList.builder()
            .client(BillList.Client.builder()
                .id(clientResponse.getId())
                .name(clientResponse.getName())
                .build())
            .bills(billList)
            .build();

        when(openBankingAccessParameters.getSubscriptionKeyOf(BILLING)).thenReturn(subscriptionKey);
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.of(access));
        when(billPaymentRestParser.toBillList(getBillResponse)).thenReturn(expectedResult);
        when(billPaymentRestClient.getBills(any(), any(), any(), any(), any())).thenReturn(Calls.response(getBillResponse));

        BillList actualResult = billPaymentRestClientAdapter.getBillList(
            payment.getClientId(), payment.getRecipientId(), payment.getServiceId());

        assertEquals(actualResult.getBills().get(0).getClientId(), expectedResult.getBills().get(0).getClientId());
    }

    @Test
    public void getMakePayment_Should_Throw_Exception_WhenAccessIsNotPresent() throws Exception {
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.empty());
        assertThrows(RestClientException.class, () -> billPaymentRestClientAdapter.makePayment(payment));
    }

    @Test
    public void getMakePayment_ShouldSucceed_WhenValidData() throws Exception {
        doNothing().when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(BillPaymentRestClientAdapter.class));
        when(openBankingAccessParameters.getSubscriptionKeyOf(BILLING)).thenReturn(subscriptionKey);
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.of(access));
        when(billPaymentRestClient.payBilling(any(), any(), anyBoolean(), any(), any(), any(), any())).thenReturn(Calls.response((Void) null));
        billPaymentRestClientAdapter.makePayment(payment);
        verify(openBankingAccessParameters, times(1)).getSubscriptionKeyOf(BILLING);
        verify(interbankAuthorizationManager, times(1)).getOpenBankingAccess();
        verify(billPaymentRestClient, times(1)).payBilling(any(), any(), anyBoolean(), any(), any(), any(), any());
    }

    @Test
    public void getMakePayment_ShouldThrowRestClientException_WhenRestClientFails() throws Exception {
        when(openBankingAccessParameters.getSubscriptionKeyOf(BILLING)).thenReturn(subscriptionKey);
        when(interbankAuthorizationManager.getOpenBankingAccess()).thenReturn(Optional.of(access));
        when(billPaymentRestClient.payBilling(any(), any(), anyBoolean(), any(), any(), any(), any())).thenReturn(Calls.response(Response.error(400,
            ResponseBody.create(null, "Error de comunicacion"))));
        doAnswer(invocation -> {
            throw new RestClientException(new IOException(), BillPaymentRestClientAdapter.class.getName(), "Error de comunicacion");
        }).when(interbankRetrofitErrorHandler).ensureSuccessful(any(), eq(BillPaymentRestClientAdapter.class));

        assertThrows(RestClientException.class, () -> billPaymentRestClientAdapter.makePayment(payment));
        verify(openBankingAccessParameters, times(1)).getSubscriptionKeyOf(BILLING);
        verify(interbankAuthorizationManager, times(1)).getOpenBankingAccess();
        verify(billPaymentRestClient, times(1)).payBilling(any(), any(), anyBoolean(), any(), any(), any(), any());
    }
}
