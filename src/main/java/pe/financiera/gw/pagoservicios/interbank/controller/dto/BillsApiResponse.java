package pe.financiera.gw.pagoservicios.interbank.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Contrato REST hacia el BS. Misma forma JSON que antes devolvía GetBillResponse (IBK).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillsApiResponse {
    private ClientApiDto client;
    private List<BillApiDto> bills;
}
