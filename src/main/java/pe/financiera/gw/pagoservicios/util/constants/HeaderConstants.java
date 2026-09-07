package pe.financiera.gw.pagoservicios.util.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    public static final String AUTH = "Authorization";
    public static final String SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String API_FORCE_SYNC = "X-Api-Force-Sync";
}
