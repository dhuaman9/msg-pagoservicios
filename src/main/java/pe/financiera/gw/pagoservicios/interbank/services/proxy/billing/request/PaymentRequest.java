package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private ClientRequest client;
    private String amount;
    private String currency;
}
