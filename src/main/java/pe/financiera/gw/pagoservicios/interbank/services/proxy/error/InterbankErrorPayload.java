package pe.financiera.gw.pagoservicios.interbank.services.proxy.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterbankErrorPayload {

    private String code;

    private String message;
}
