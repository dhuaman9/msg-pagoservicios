package pe.financiera.gw.pagoservicios.interbank.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillApiDto {
    private String currency;
    private String totalAmount;
    private String discount;
    private String feeAmount;
    private String commission;
    private String dueDate;
    private String id;
}
