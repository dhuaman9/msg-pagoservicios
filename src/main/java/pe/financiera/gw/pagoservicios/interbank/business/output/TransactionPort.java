package pe.financiera.gw.pagoservicios.interbank.business.output;

import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;

public interface TransactionPort {

    BillStatusEntity getBillStatus(String operationNumber) throws IOException, RestClientException, InterbankApiException;
}
