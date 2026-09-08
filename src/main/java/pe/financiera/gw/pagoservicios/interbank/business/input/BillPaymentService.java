package pe.financiera.gw.pagoservicios.interbank.business.input;

import pe.financiera.gw.pagoservicios.interbank.business.domain.BillList;
import pe.financiera.gw.pagoservicios.interbank.business.domain.DirectPaymentV2;
import pe.financiera.gw.pagoservicios.interbank.business.domain.PaymentV2;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

/**
 * Puerto de entrada (casos de uso) expuestos al BS vía REST.
 */
public interface BillPaymentService {

    void makePaymentV2(PaymentV2 payment) throws IOException, RestClientException, InterbankApiException;

    BillList getBills(String clientId, String recipientId, String serviceId) throws IOException, RestClientException, InterbankApiException;

    void makeDirectPaymentV2(DirectPaymentV2 directPayment) throws IOException, RestClientException, InterbankApiException;
}
