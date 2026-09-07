package pe.financiera.gw.pagoservicios.util.exception.interbank;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InterbankApiExceptionTest {

    @Test
    void fromIbkPayload_shouldPassThroughCodeAndMessage() {
        InterbankApiException exception = InterbankApiException.fromIbkPayload(
            "07.01.03", "BillNotFoundException", HttpStatus.BAD_REQUEST
        );

        assertEquals("07.01.03", exception.getErrorCode());
        assertEquals("BillNotFoundException", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void fromIbkPayload_shouldPassThroughUnknownCode() {
        InterbankApiException exception = InterbankApiException.fromIbkPayload(
            "99.99.99", "SomethingWeird", HttpStatus.BAD_REQUEST
        );

        assertEquals("99.99.99", exception.getErrorCode());
        assertEquals("SomethingWeird", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void fromIbkPayload_shouldDefaultToInternalServerErrorWhenStatusIsMissing() {
        InterbankApiException exception = InterbankApiException.fromIbkPayload("01.01.01", "Unexpected Error");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    void fromIbkPayload_shouldAllowNullCodeAndMessage() {
        InterbankApiException exception = InterbankApiException.fromIbkPayload(null, null, HttpStatus.BAD_GATEWAY);

        assertNull(exception.getErrorCode());
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.BAD_GATEWAY, exception.getHttpStatus());
    }
}
