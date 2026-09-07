package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Bill;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.BillResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.ClientResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.GetBillResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BillPaymentRestParser {

    public BillList toBillList(GetBillResponse response) {
        if (response == null) {
            return BillList.builder()
                .client(null)
                .bills(Collections.emptyList())
                .build();
        }
        return BillList.builder()
            .client(toClient(response.getClient()))
            .bills(getBillResponseToDomain(response))
            .build();
    }

    public List<Bill> getBillResponseToDomain(GetBillResponse billList) {
        String clientId = Optional.ofNullable(billList)
            .map(GetBillResponse::getClient)
            .map(ClientResponse::getId)
            .orElse(null);

        return Optional.ofNullable(billList)
            .map(GetBillResponse::getBills)
            .map(List::stream)
            .orElse(Stream.empty())
            .map(x -> this.billResponseToDomain(x, clientId))
            .collect(Collectors.toList());
    }

    private BillList.Client toClient(ClientResponse clientResponse) {
        if (clientResponse == null) {
            return null;
        }
        return BillList.Client.builder()
            .id(clientResponse.getId())
            .name(clientResponse.getName())
            .build();
    }

    private Bill billResponseToDomain(BillResponse billResponse, String clientId) {
        return Optional.ofNullable(billResponse)
            .map(billObj ->
                Bill.builder()
                    .clientId(clientId)
                    .currency(billObj.getCurrency())
                    .totalAmount(billObj.getTotalAmount())
                    .discount(billObj.getDiscount())
                    .feeAmount(billObj.getFeeAmount())
                    .commission(billObj.getCommission())
                    .dueDate(billObj.getDueDate())
                    .id(billObj.getId())
                    .build()
            )
            .orElse(Bill.builder().build());
    }
}
