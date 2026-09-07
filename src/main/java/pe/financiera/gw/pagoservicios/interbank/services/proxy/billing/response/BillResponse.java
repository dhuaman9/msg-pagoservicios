package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private String currency;
    private String totalAmount;
    private String discount;
    private String feeAmount;
    private String commission;
    private String dueDate;
    private String id;
}
