package pe.financiera.gw.pagoservicios.util.exception.Base;

import lombok.Getter;

@Getter
public abstract class BaseException extends Exception {

    protected String errorCode;

    protected Exception cause;

    protected String entity;

    protected String detail;

    public BaseException(
        String errorCode,
        String message,
        Exception cause,
        String entity
    ) {
        super(message);
        this.errorCode = errorCode;
        this.cause = cause;
        this.entity = entity;
    }

    public BaseException(
        String errorCode,
        String message,
        Exception cause,
        String entity,
        String detail
    ) {
        this(errorCode,message, cause,entity);
        this.detail = detail;
    }
}
