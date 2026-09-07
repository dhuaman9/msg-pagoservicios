package pe.financiera.gw.pagoservicios.util.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.SecurityRestClientAdapter;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InterbankAuthorizationManagerTest {
    @Mock
    private JWTParser jwtParser;

    @Mock
    private SecurityRestClientAdapter securityRestClientAdapter;

    @InjectMocks
    private InterbankAuthorizationManager authorizationManager;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void getOpenBankingAccessTest() throws IOException, RestClientException, InterbankApiException {
        Long dF = LocalDate.now().plusYears(20).toEpochDay();
        Long d = LocalDate.now().toEpochDay();
        OpenBankingAccess open = OpenBankingAccess
            .builder()
            .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ0b2tlbjphcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uOjRZekYzZEdiSmdWdEU2eW4iXSwiZXhwIjoxNTY4OTA4MTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1RSVVNURURfQ0xJRU5UIiwiUk9MRV9DTElFTlQiXSwianRpIjoiNjcyZjU1NGQtNDM2ZC00ZTQ3LWEzZTAtZGRhM2YzZGNiNzZhIiwiY2xpZW50X2lkIjoiNFl6RjNkR2JKZ1Z0RTZ5biJ9.p00xpRwMC0jWqIOytnKzGy1QtvbH_Qz9FdJDuaAAyyE")
            .creationTime(d)
            .expiresIn(dF)
            .build();
        JWTParser.Access newAccess = new JWTParser.Access(1905545739000L);


        when(securityRestClientAdapter.getAccess()).thenReturn(open);
        when(jwtParser.parse(anyString())).thenReturn(Optional.ofNullable(newAccess));

        authorizationManager.getOpenBankingAccess();
        Optional<OpenBankingAccess> access = authorizationManager.getOpenBankingAccess();
        assertTrue(access.isPresent());
    }
}
