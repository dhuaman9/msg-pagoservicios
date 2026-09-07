package pe.financiera.gw.pagoservicios.util.exception;

public class MissingAccessTokenException extends RuntimeException {

    public MissingAccessTokenException(String message) {
        super(message);
    }
}
