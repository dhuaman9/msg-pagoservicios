package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BillStatusEntity {

    private final String operationNumber;
    private final String status;
    private final String paymentId;
    private final Error error;

    @Builder
    @Getter
    public static class Error {
        private final String message;
        private final String code;
    }
}
