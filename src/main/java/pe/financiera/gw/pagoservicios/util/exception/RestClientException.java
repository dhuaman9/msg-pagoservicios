package pe.financiera.gw.pagoservicios.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pe.financiera.gw.pagoservicios.util.exception.Base.BaseException;

import static pe.financiera.gw.pagoservicios.util.exception.Base.GenericErrors.GE_09;

@Getter
public class RestClientException extends BaseException {

    private final HttpStatus httpStatus;

    public RestClientException(Exception cause, String entity, String detail) {
        this(cause, entity, detail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public RestClientException(Exception cause, String entity, String detail, HttpStatus httpStatus) {
        super(
            GE_09.name(),
            GE_09.getMessage(),
            cause,
            entity,
            detail
        );
        this.httpStatus = httpStatus;
    }

}
