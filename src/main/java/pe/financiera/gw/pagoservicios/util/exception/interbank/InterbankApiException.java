package pe.financiera.gw.pagoservicios.util.exception.interbank;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pe.financiera.gw.pagoservicios.util.exception.Base.BaseException;

@Getter
public class InterbankApiException extends BaseException {

    private final HttpStatus httpStatus;

    public InterbankApiException(String code, String message, HttpStatus httpStatus) {
        super(code, message, null, "interbank-api", null);
        this.httpStatus = httpStatus;
    }

    public static InterbankApiException fromIbkPayload(String code, String message) {
        return fromIbkPayload(code, message, null);
    }

    public static InterbankApiException fromIbkPayload(String code, String message, HttpStatus ibkStatus) {
        return new InterbankApiException(
            code,
            message,
            ibkStatus != null ? ibkStatus : HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
