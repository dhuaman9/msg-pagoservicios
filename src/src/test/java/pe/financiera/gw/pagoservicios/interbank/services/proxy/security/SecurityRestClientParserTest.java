package pe.financiera.gw.pagoservicios.interbank.services.proxy.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.response.TokenOkResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;



@ExtendWith(MockitoExtension.class)
public class SecurityRestClientParserTest {

    private TokenOkResponse tokenOkResponse;

    private OpenBankingAccess openBankingAccess;

    private SecurityRestClientParser securityRestClientParser;

    @BeforeEach
    public void init() {
        tokenOkResponse = TokenOkResponse.builder()
            .accessToken("token")
            .tokenType("abc")
            .expiresIn(3600L)
            .jti("abc")
            .scope("abc")
            .build();

        openBankingAccess = OpenBankingAccess.builder()
            .accessToken("token")
            .tokenType("abc")
            .expiresIn(3600L)
            .jti("abc")
            .scope("abc")
            .build();
        securityRestClientParser = new SecurityRestClientParser();
    }

    @Test
    public void convertResponseToDomain() {
        //then
        OpenBankingAccess openBankingAccessResult = securityRestClientParser.convertResponseToDomain(tokenOkResponse);
        // assert
        assertEquals(openBankingAccess.getAccessToken(),openBankingAccessResult.getAccessToken());
        assertEquals(openBankingAccess.getTokenType(),openBankingAccessResult.getTokenType());
        assertEquals(openBankingAccess.getExpiresIn(),openBankingAccessResult.getExpiresIn());
        assertEquals(openBankingAccess.getJti(),openBankingAccessResult.getJti());
        assertEquals(openBankingAccess.getScope(),openBankingAccessResult.getScope());
    }
}
