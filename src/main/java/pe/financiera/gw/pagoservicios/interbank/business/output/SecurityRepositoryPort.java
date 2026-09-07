package pe.financiera.gw.pagoservicios.interbank.business.output;

import java.io.IOException;

import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

public interface SecurityRepositoryPort {

    OpenBankingAccess getAccess() throws IOException, RestClientException, InterbankApiException;

}
