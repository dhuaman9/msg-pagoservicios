package pe.financiera.gw.pagoservicios.interbank.business.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Payment {
    private String recipientId;
    private String serviceId;
    private String billId;
    private String correlationId;
    private String clientId;

    @JsonCreator
    public Payment(
        @JsonProperty("recipientId") String recipientId,
        @JsonProperty("serviceId") String serviceId,
        @JsonProperty("billId") String billId,
        @JsonProperty("correlationId") String correlationId,
        @JsonProperty("clientId") String clientId
    ){
        this.recipientId = recipientId;
        this.serviceId = serviceId;
        this.billId = billId;
        this.correlationId = correlationId;
        this.clientId = clientId;
    }
}
