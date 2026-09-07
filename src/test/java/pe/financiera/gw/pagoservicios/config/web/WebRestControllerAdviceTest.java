package pe.financiera.gw.pagoservicios.config.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.dto.ApiErrorResponse;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebRestControllerAdviceTest {

    private WebRestControllerAdvice webRestControllerAdvice;

    @Mock
    private WebLogger webLogger;

    @Mock
    private MethodParameter parameter;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        webRestControllerAdvice = new WebRestControllerAdvice(webLogger);
    }

    @Test
    public void handleMethodArgumentNotValid() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new FieldError("User", "userId", "Not Valid"));
        when(bindingResult.getAllErrors()).thenReturn(errors);
        MethodArgumentNotValidException methodArgumentNotValidException
            = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<?> result = webRestControllerAdvice.handleMethodArgumentNotValid(methodArgumentNotValidException);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value(), 0);
    }

    @Test
    public void handleMalformedRequest() {
        HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException =
            new HttpMediaTypeNotSupportedException(new MediaType("''"), new ArrayList<MediaType>() {{
                add(MediaType.APPLICATION_JSON);
            }});
        ResponseEntity<?> result = webRestControllerAdvice.handleMalformedRequest(httpMediaTypeNotSupportedException);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode().value(), 0);
    }

    @Test
    public void handleRestClientException() {
        RestClientException restClientException = new RestClientException(new Exception("error"), "entity", "detail");
        ResponseEntity<?> result = webRestControllerAdvice.handleRestClientException(restClientException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatusCode().value(), 0);
    }

    @Test
    public void handleRestClientException_shouldPreserveIbkStatus() {
        RestClientException restClientException = new RestClientException(
            new Exception("error"), "entity", "detail", HttpStatus.BAD_GATEWAY
        );

        ResponseEntity<?> result = webRestControllerAdvice.handleRestClientException(restClientException);

        assertEquals(HttpStatus.BAD_GATEWAY.value(), result.getStatusCode().value(), 0);
    }

    @Test
    public void handleInterbankApiException() {
        InterbankApiException exception = new InterbankApiException(
            "07.01.03",
            "BillNotFoundException",
            HttpStatus.NOT_FOUND
        );

        ResponseEntity<ApiErrorResponse> result = webRestControllerAdvice.handleInterbankApiException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());
        assertEquals("07.01.03", result.getBody().getCode());
        assertEquals("BillNotFoundException", result.getBody().getMessage());
    }
}
