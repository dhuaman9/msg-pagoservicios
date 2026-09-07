package pe.financiera.gw.pagoservicios.util.exception;

import lombok.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pe.financiera.gw.pagoservicios.util.exception.Base.BaseException;
import pe.financiera.gw.pagoservicios.util.exception.validation.FieldValidation;

import java.util.ArrayList;
import java.util.List;

import static pe.financiera.gw.pagoservicios.util.exception.Base.GenericErrors.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorWrapper {

    private String entity;

    private String message;

    private String code;

    private List<FieldValidation> errors;

    public ErrorWrapper buildFromError(MethodArgumentNotValidException methodArgumentNotValidException) {
        ErrorWrapper errorWrapper = new ErrorWrapper();
        errorWrapper.setEntity(methodArgumentNotValidException.getBindingResult().getObjectName());
        errorWrapper.setMessage(GE_01.getMessage());
        errorWrapper.setCode(GE_01.name());
        List<FieldValidation> errorsList = new ArrayList<>();
        for (ObjectError objectError : methodArgumentNotValidException.getBindingResult().getAllErrors()) {
            errorsList.add(
                new FieldValidation(((FieldError) objectError).getField(), objectError.getDefaultMessage()));
        }
        errorWrapper.setErrors(errorsList);
        return errorWrapper;
    }

    public ErrorWrapper buildFromError(BaseException ex) {
        return ErrorWrapper.builder()
            .code(ex.getErrorCode())
            .message(ex.getMessage())
            .entity(ex.getEntity())
            .build();
    }
}
