package pe.financiera.gw.pagoservicios.interbank.controller.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Bill;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.BillsApiResponse;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BillsApiMapperTest {

    private BillsApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BillsApiMapper();
    }

    @Test
    void toApiResponse_shouldMapClientAndBills() {
        BillList billList = BillList.builder()
            .client(BillList.Client.builder().id("A1580040").name("Juan Perez").build())
            .bills(Arrays.asList(
                Bill.builder()
                    .id("00000003")
                    .currency("PEN")
                    .totalAmount("91.42")
                    .discount("0.00")
                    .feeAmount("0.00")
                    .commission("0.00")
                    .dueDate("2018-08-18")
                    .build(),
                Bill.builder().id("00000004").currency("USD").build()))
            .build();

        BillsApiResponse response = mapper.toApiResponse(billList);

        assertEquals("A1580040", response.getClient().getId());
        assertEquals("Juan Perez", response.getClient().getName());
        assertEquals(2, response.getBills().size());
        assertEquals("00000003", response.getBills().get(0).getId());
        assertEquals("PEN", response.getBills().get(0).getCurrency());
        assertEquals("91.42", response.getBills().get(0).getTotalAmount());
        assertEquals("2018-08-18", response.getBills().get(0).getDueDate());
        assertEquals("00000004", response.getBills().get(1).getId());
    }

    @Test
    void toApiResponse_shouldReturnEmptyBillsWhenBillListIsNull() {
        BillsApiResponse response = mapper.toApiResponse(null);

        assertNull(response.getClient());
        assertTrue(response.getBills().isEmpty());
    }

    @Test
    void toApiResponse_shouldHandleMissingClientAndBills() {
        BillsApiResponse response = mapper.toApiResponse(BillList.builder().build());

        assertNull(response.getClient());
        assertTrue(response.getBills().isEmpty());
    }
}
