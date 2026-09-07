package pe.financiera.gw.pagoservicios.interbank.business.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenBankingAccess {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String scope;
    private String jti;
    private Long creationTime;

    public String getAuthorization() {
        return "Bearer ".concat(this.accessToken);
    }

}
