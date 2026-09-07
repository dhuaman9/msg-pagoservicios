package pe.financiera.gw.pagoservicios.util.exception.Base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pe.financiera.gw.pagoservicios.util.exception.validation.Errors;

@Getter
@AllArgsConstructor
public enum GenericErrors implements Errors {

    GE_00("General Error Exception"),
    GE_01("Data is not valid"),
    GE_02("Entity not found with id %s"),
    GE_03("None entity was found for query"),
    GE_04("Unsupported Media Type"),
    GE_05("Bad Body Request"),
    GE_06("UNAUTHORIZED User %s"),
    GE_07("UNAUTHORIZED, the Access token has expired"),
    GE_08("UNAUTHORIZED, Invalid Token"),
    GE_09("Error on Retrofit request");

    private String message;

    @Override
    public String getMessage() {
        return message;
    }

}
