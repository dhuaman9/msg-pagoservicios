package pe.financiera.gw.pagoservicios.util.exception;

import pe.financiera.gw.pagoservicios.util.exception.Base.BaseException;

import static pe.financiera.gw.pagoservicios.util.exception.Base.GenericErrors.GE_05;

public class BadRequestException  extends BaseException {

    public BadRequestException(Exception cause) {
        super(
            GE_05.name(),
            GE_05.getMessage(),
            cause,
            null
        );
    }
}
