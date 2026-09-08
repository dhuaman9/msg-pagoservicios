package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.request.BillPaymentRequest;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.GetBillResponse;
import retrofit2.Call;
import retrofit2.http.*;

import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.API_FORCE_SYNC;
import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.AUTH;
import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.CORRELATION_ID;
import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.SUBSCRIPTION_KEY;

public interface BillPaymentRestClient {

    @POST("/billpayments/v1/recipients/{recipientsId}/services/{serviceId}/bills/{billId}/payments")
    Call<Void> payBilling(
        @Header(SUBSCRIPTION_KEY) String subscriptionKey,
        @Header(CORRELATION_ID) String correlationId,
        @Header(API_FORCE_SYNC) boolean forceSync,
        @Header(AUTH) String auth,
        @Path("recipientsId") String recipientsId,
        @Path("serviceId") String serviceId,
        @Path("billId") String billId
    );

    @GET("/billpayments/v1/recipients/{recipientsId}/services/{serviceId}/bills")
    Call<GetBillResponse> getBills(
        @Header(SUBSCRIPTION_KEY) String subscriptionKey,
        @Header(AUTH) String auth,
        @Path("recipientsId") String recipientsId,
        @Path("serviceId") String serviceId,
        @Query("clientId") String clientId
    );

    @POST("/billpayments/v1/recipients/{recipientsId}/services/{serviceId}/payments")
    Call<Void> payDirectBilling(
        @Header(SUBSCRIPTION_KEY) String subscriptionKey,
        @Header(CORRELATION_ID) String correlationId,
        @Header(API_FORCE_SYNC) boolean forceSync,
        @Header(AUTH) String auth,
        @Path("recipientsId") String recipientsId,
        @Path("serviceId") String serviceId,
        @Body BillPaymentRequest billPaymentRequest
    );



}
