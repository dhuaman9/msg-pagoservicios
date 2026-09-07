package pe.financiera.gw.pagoservicios.interbank.business.output;

import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.Payment;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

/**
 * Puerto de salida hacia Interbank (billing).
 */
public interface BillPaymentPort {

    void makePayment(Payment payment) throws IOException, RestClientException, InterbankApiException;

    BillList getBillList(String clientId, String recipientId, String serviceId) throws IOException, RestClientException, InterbankApiException;
}
