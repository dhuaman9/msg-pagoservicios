package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import org.springframework.stereotype.Component;

import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.response.TokenOkResponse;

import java.util.HashMap;

@Component
public class SecurityRestClientParser {

    public OpenBankingAccess convertResponseToDomain(TokenOkResponse tokenOkResponse) {
        return OpenBankingAccess.builder()
            .accessToken(tokenOkResponse.getAccessToken())
            .tokenType(tokenOkResponse.getTokenType())
            .expiresIn(tokenOkResponse.getExpiresIn())
            .scope(tokenOkResponse.getScope())
            .jti(tokenOkResponse.getJti())
            .build();
    }
}
