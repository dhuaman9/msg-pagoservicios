package pe.financiera.gw.pagoservicios.interbank.services.proxy.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterbankRetrofitErrorHandlerTest {

    private static final MediaType JSON = MediaType.parse("application/json");

    private InterbankRetrofitErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new InterbankRetrofitErrorHandler(new InterbankErrorParser(new ObjectMapper()));
    }

    @Test
    void ensureSuccessful_shouldDoNothingOnSuccessfulResponse() {
        assertDoesNotThrow(() ->
            handler.ensureSuccessful(Response.success(null), InterbankRetrofitErrorHandlerTest.class));
    }

    @Test
    void ensureSuccessful_shouldPassThroughKnownIbkError() {
        Response<Void> response = Response.error(404,
            ResponseBody.create("{\"error\":{\"code\":\"07.01.03\",\"message\":\"BillNotFoundException\"}}", JSON));

        InterbankApiException exception = assertThrows(InterbankApiException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));

        assertEquals("07.01.03", exception.getErrorCode());
        assertEquals("BillNotFoundException", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void ensureSuccessful_shouldPassThroughFlatIbkError() {
        Response<Void> response = Response.error(403,
            ResponseBody.create("{\"code\":\"07.01.02\",\"message\":\"PaymentLimitException\"}", JSON));

        InterbankApiException exception = assertThrows(InterbankApiException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));

        assertEquals("07.01.02", exception.getErrorCode());
        assertEquals("PaymentLimitException", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
    }

    @Test
    void ensureSuccessful_shouldPreserveIbkHttpStatus() {
        Response<Void> response = Response.error(400,
            ResponseBody.create("{\"error\":{\"code\":\"07.01.03\",\"message\":\"BillNotFoundException\"}}", JSON));

        InterbankApiException exception = assertThrows(InterbankApiException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void ensureSuccessful_shouldFallbackToRestClientExceptionWhenBodyIsNotParseable() {
        Response<Void> response = Response.error(500,
            ResponseBody.create("Error de comunicacion", JSON));

        RestClientException exception = assertThrows(RestClientException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));

        assertEquals("Error de comunicacion", exception.getDetail());
    }

    @Test
    void ensureSuccessful_shouldPreserveIbkStatusWhenBodyIsNotParseable() {
        Response<Void> response = Response.error(502,
            ResponseBody.create("Error de gateway", JSON));

        RestClientException exception = assertThrows(RestClientException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
    }

    @Test
    void ensureSuccessful_shouldFallbackWhenBodyIsEmpty() {
        Response<Void> response = Response.error(502, ResponseBody.create("", JSON));

        assertThrows(RestClientException.class,
            () -> handler.ensureSuccessful(response, InterbankRetrofitErrorHandlerTest.class));
    }
}
