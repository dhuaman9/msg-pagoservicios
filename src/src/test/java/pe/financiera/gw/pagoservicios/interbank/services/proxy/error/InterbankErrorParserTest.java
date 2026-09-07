package pe.financiera.gw.pagoservicios.interbank.services.proxy.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterbankErrorParserTest {

    private InterbankErrorParser parser;

    @BeforeEach
    void setUp() {
        parser = new InterbankErrorParser(new ObjectMapper());
    }

    @Test
    void parse_shouldPassThroughKnownIbkError() {
        String body = """
            {"error":{"code":"07.01.03","message":"BillNotFoundException"}}
            """;

        InterbankApiException exception = parser.parse(body).orElseThrow();

        assertEquals("07.01.03", exception.getErrorCode());
        assertEquals("BillNotFoundException", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    void parse_shouldReturnEmptyForUnparseableBody() {
        assertTrue(parser.parse("Error de comunicacion").isEmpty());
    }

    @Test
    void parse_shouldPreserveIbkHttpStatus() {
        String body = """
            {"error":{"code":"07.01.03","message":"BillNotFoundException"}}
            """;

        InterbankApiException exception = parser.parse(body, HttpStatus.BAD_REQUEST).orElseThrow();

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}
