package pe.financiera.gw.pagoservicios.interbank.controller.mapper;

import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Bill;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.BillApiDto;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.BillsApiResponse;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.ClientApiDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillsApiMapper {

    public BillsApiResponse toApiResponse(BillList billList) {
        if (billList == null) {
            return BillsApiResponse.builder()
                .client(null)
                .bills(Collections.emptyList())
                .build();
        }
        return BillsApiResponse.builder()
            .client(toClientDto(billList.getClient()))
            .bills(toBillDtos(billList.getBillsOrEmpty()))
            .build();
    }

    private ClientApiDto toClientDto(BillList.Client client) {
        if (client == null) {
            return null;
        }
        return ClientApiDto.builder()
            .id(client.getId())
            .name(client.getName())
            .build();
    }

    private List<BillApiDto> toBillDtos(List<Bill> bills) {
        return bills.stream()
            .map(bill -> BillApiDto.builder()
                .id(bill.getId())
                .currency(bill.getCurrency())
                .totalAmount(bill.getTotalAmount())
                .discount(bill.getDiscount())
                .feeAmount(bill.getFeeAmount())
                .commission(bill.getCommission())
                .dueDate(bill.getDueDate())
                .build())
            .collect(Collectors.toList());
    }
}
