package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectPayment {

    private String currency;
    private String amount;
    private String clientId;
    private String recipientId;
    private String serviceId;
    private String correlationId;
}
