package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetBillResponse {
    private ClientResponse client;
    private List<BillResponse> bills;
}
