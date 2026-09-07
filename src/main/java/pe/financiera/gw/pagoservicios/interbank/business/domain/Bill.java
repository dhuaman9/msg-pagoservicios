package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bill {
    private String clientId;
    private String currency;
    private String totalAmount;
    private String discount;
    private String feeAmount;
    private String commission;
    private String dueDate;
    private String id;
}
