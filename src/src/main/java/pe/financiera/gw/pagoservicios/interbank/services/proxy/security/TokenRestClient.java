package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.response.TokenOkResponse;
import retrofit2.Call;
import retrofit2.http.*;

import static pe.financiera.gw.pagoservicios.util.constants.HeaderConstants.*;

public interface TokenRestClient {

    @FormUrlEncoded
    @POST("/security/v1/oauth/token")
    Call<TokenOkResponse> getAccess(
        @Header(AUTH) String authorization,
        @Field("grant_type") String grantType);
}
