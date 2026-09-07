package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.Correlation;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.DataRequest;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.ErrorRequest;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.InterbankBillStatusResponse;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.Payment;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class TransactionRestClientParserTest {

    @InjectMocks
    private TransactionRestClientParser parser;

    @Test
    public void testConvertToDomain_GivenValidInputs_ShouldReturnBillStatusEntity() throws RestClientException {
        final String status = "STATUS", paymentId = "PAYMENT_ID", message = "MESSAGE", code = "CODE",
            operationNumber = "OPERATION_NUMBER";
        final DataRequest dataRequest = new DataRequest(new Payment(paymentId));
        final ErrorRequest errorRequest = new ErrorRequest(message, code);
        final Correlation correlation = new Correlation(status, dataRequest, errorRequest, operationNumber);
        final InterbankBillStatusResponse response = new InterbankBillStatusResponse(correlation);

        final BillStatusEntity parsed = parser.convertToDomain(response);
        assertEquals(operationNumber, parsed.getOperationNumber());
        assertEquals(status, parsed.getStatus());
        assertEquals(paymentId, parsed.getPaymentId());
        assertEquals(errorRequest.getMessage(), parsed.getError().getMessage());
        assertEquals(errorRequest.getCode(), parsed.getError().getCode());
    }
}
