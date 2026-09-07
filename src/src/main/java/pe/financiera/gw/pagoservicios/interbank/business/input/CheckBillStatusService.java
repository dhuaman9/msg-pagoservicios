package pe.financiera.gw.pagoservicios.interbank.business.input;

import pe.financiera.gw.pagoservicios.interbank.event.message.third.party.TransactionEvent;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

public interface CheckBillStatusService {

    void checkBillStatusV2(TransactionEvent event, Integer nbRetry) throws IOException, RestClientException, InterbankApiException;
}
