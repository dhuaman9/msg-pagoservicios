package pe.financiera.gw.pagoservicios.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.financiera.gw.pagoservicios.util.exception.BadRequestException;
import pe.financiera.gw.pagoservicios.util.exception.Base.BaseException;
import pe.financiera.gw.pagoservicios.util.exception.ErrorWrapper;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.dto.ApiErrorResponse;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import static pe.financiera.gw.pagoservicios.util.exception.Base.GenericErrors.GE_09;
import static pe.financiera.gw.pagoservicios.util.exception.Base.GenericErrors.GE_01;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebRestControllerAdvice extends WebResponseBodyAdvice {

    @Autowired
    public WebRestControllerAdvice(WebLogger webLogger) {
        super(webLogger);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorWrapper> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorWrapper.builder().build().buildFromError(ex));
    }

    @ExceptionHandler({
        HttpMediaTypeNotSupportedException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorWrapper> handleMalformedRequest(Exception ex) {
        return handleBadRequest(new BadRequestException(ex));
    }

    @ExceptionHandler({
        InterbankApiException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInterbankApiException(InterbankApiException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler({
        RestClientException.class
    })
    public ResponseEntity<ApiErrorResponse> handleRestClientException(RestClientException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiErrorResponse.of(
                GE_09.name(),
                GE_09.getMessage(),
                ex.getDetail() != null ? ex.getDetail() : GE_09.getMessage()
            ));
    }

    @ExceptionHandler({
        BadRequestException.class
    })
    public ResponseEntity<ErrorWrapper> handleBadRequest(BaseException ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorWrapper> buildResponseEntity(BaseException ex, HttpStatus status) {
        return ResponseEntity.status(status)
            .body(ErrorWrapper.builder().build().buildFromError(ex));
    }

    //nuevos metodos
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        // cubre: clientId AUSENTE de la query string
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse.of(GE_01.name(), "Falta el parámetro: " + ex.getParameterName()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        // cubre: recipientId, serviceId o clientId VACÍOS/en blanco
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiErrorResponse.of(GE_01.name(), ex.getMessage()));
    }
}
