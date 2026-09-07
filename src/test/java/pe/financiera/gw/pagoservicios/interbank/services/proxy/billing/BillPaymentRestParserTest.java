package pe.financiera.gw.pagoservicios.interbank.services.proxy.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Bill;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.billing.response.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BillPaymentRestParserTest {

    @Mock
    private BillPaymentRestParser billPaymentRestParser;

    private List<Bill> billList;

    private List<BillResponse> billResponsesList;

    @BeforeEach
    public void init() {
        billPaymentRestParser = new BillPaymentRestParser();
        billList = new ArrayList<>();
        billResponsesList = new ArrayList<>();
    }

    @Test
    public void getBillResponseToDomain() {
        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setId("A1580040");
        BillResponse billObj = new BillResponse();
        billObj.setCurrency("PEN");
        billObj.setTotalAmount("91.42");
        billObj.setDiscount("91.42");
        billObj.setFeeAmount("0.00");
        billObj.setCommission("0.00");
        billObj.setDueDate("2018-08-18");
        billObj.setId("00000003");
        billResponsesList.add(billObj);

        billList.add(Bill.builder()
            .clientId(clientResponse.getId())
            .currency(billObj.getCurrency())
            .totalAmount(billObj.getTotalAmount())
            .discount(billObj.getDiscount())
            .feeAmount(billObj.getFeeAmount())
            .commission(billObj.getCommission())
            .dueDate(billObj.getDueDate())
            .id(billObj.getId())
            .build()
        );

        GetBillResponse getBillResponse = new GetBillResponse();
        getBillResponse.setBills(billResponsesList);
        getBillResponse.setClient(clientResponse);

        List<Bill> expectedBillList = billPaymentRestParser.getBillResponseToDomain(getBillResponse);
        assertEquals(expectedBillList.get(0).getClientId(), billList.get(0).getClientId());

        BillList domainList = billPaymentRestParser.toBillList(getBillResponse);
        assertNotNull(domainList.getClient());
        assertEquals("A1580040", domainList.getClient().getId());
        assertEquals(1, domainList.getBillsOrEmpty().size());
    }

}
