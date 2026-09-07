package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.InterbankBillStatusResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.API_FORCE_SYNC;
import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.AUTH;
import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.SUBSCRIPTION_KEY;

public interface TransactionRestClient {

    @GET("/applications/v1/correlations/{correlationId}")
    Call<InterbankBillStatusResponse> billStatus(
        @Header(SUBSCRIPTION_KEY) String subscriptionKey,
        @Header(API_FORCE_SYNC) boolean forceSync,
        @Header(AUTH) String auth,
        @Path("correlationId") String correlationId);
}
