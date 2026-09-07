package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentV2 {
    private String recipientId;
    private String serviceId;
    private String billId;
    private String correlationId;
    private String clientId;
    private String operationId;
    private String operationNumber;
    private String contract;
    private String currencyType;
    private String transferAmount;
    private BigDecimal amount;
    private String transferType;
    private String sourceType;
    private String userId;
    private String documentType;
    private String documentNumber;
    private String creationDate;
    private String tokenTunki;
    private String referenceNumber;
    private String externalAccountId;
    private String commandTrigger;
    private String eventTag;
    private Map<String, Object> customProperties;
}

