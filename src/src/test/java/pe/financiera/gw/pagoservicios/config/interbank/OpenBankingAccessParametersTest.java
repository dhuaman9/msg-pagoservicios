package pe.financiera.gw.pagoservicios.config.interbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenBankingAccessParametersTest {

    private OpenBankingAccessParameters openBankingAccessParameters;

    @BeforeEach
    public void init() {
        openBankingAccessParameters = new OpenBankingAccessParameters();
    }

    @Test
    public void getBasicAuth() throws NoSuchFieldException {
        ReflectionTestUtils.setField(this.openBankingAccessParameters, "applicationId", "abc");
        ReflectionTestUtils.setField(this.openBankingAccessParameters, "password", "abc");
        String authResult = openBankingAccessParameters.getBasicAuth();
        assertEquals("Basic YWJjOmFiYw==", authResult);
    }
}
