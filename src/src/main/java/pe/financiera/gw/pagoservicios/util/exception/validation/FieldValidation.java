package pe.financiera.gw.pagoservicios.util.exception.validation;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldValidation {

    private String field;
    private String message;

}
