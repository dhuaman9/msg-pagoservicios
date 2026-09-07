package pe.financiera.gw.pagoservicios.interbank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.input.BillPaymentService;
import pe.financiera.gw.pagoservicios.interbank.controller.dto.BillsApiResponse;
import pe.financiera.gw.pagoservicios.interbank.controller.mapper.BillsApiMapper;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicesPaymentControllerTest {

    private PaymentV2 paymentV2;

    @Mock
    private BillPaymentService billPaymentService;

    @Mock
    private BillsApiMapper billsApiMapper;

    @InjectMocks
    private ServicesPaymentController servicesPaymentController;

    @BeforeEach
    public void init() {
        paymentV2 = PaymentV2.builder()
            .recipientId("01006")
            .serviceId("01")
            .billId("00000005")
            .correlationId("123456")
            .clientId("987123456")
            .build();
    }

    @Test
    public void makeBillPaymentV2Test() throws IOException, RestClientException, InterbankApiException {
        doNothing().when(billPaymentService).makePaymentV2(any());
        servicesPaymentController.makeBillPaymentV2(paymentV2);
        verify(billPaymentService, times(1)).makePaymentV2(any());
    }

    @Test
    public void getBills() throws IOException, RestClientException, InterbankApiException {
        BillList billList = BillList.builder().build();
        BillsApiResponse apiResponse = BillsApiResponse.builder().build();
        when(billPaymentService.getBills(anyString(), anyString(), anyString())).thenReturn(billList);
        when(billsApiMapper.toApiResponse(billList)).thenReturn(apiResponse);
        servicesPaymentController.getBills("01006", "01", "987123456");
        verify(billPaymentService, times(1)).getBills(anyString(), anyString(), anyString());
        verify(billsApiMapper, times(1)).toApiResponse(billList);
    }
}
