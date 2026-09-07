package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRequest {

    private String message;
    private String code;
}
