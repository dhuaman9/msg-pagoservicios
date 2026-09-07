package pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction;

import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.domain.BillStatusEntity;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.transaction.response.InterbankBillStatusResponse;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;

import java.io.IOException;
import java.util.Optional;

@Component
public class TransactionRestClientParser {

    public BillStatusEntity convertToDomain(final InterbankBillStatusResponse response) throws RestClientException {
        return Optional.ofNullable(response)
            .map(billStatusResponse -> BillStatusEntity.builder()
                .operationNumber(billStatusResponse.getCorrelation().getId())
                .status(billStatusResponse.getCorrelation().getStatus())
                .paymentId(Optional.ofNullable(billStatusResponse.getCorrelation().getData())
                    .map(data -> data.getPayment().getId())
                    .orElse(null))
                .error(Optional.ofNullable(response.getCorrelation().getError())
                    .map(error -> BillStatusEntity.Error.builder()
                        .message(error.getMessage())
                        .code(error.getCode())
                        .build())
                    .orElse(null))
                .build())
            .orElseThrow(() -> new RestClientException(new IOException(), this.getClass().getName(), "Response body cannot be null"));
    }
}
