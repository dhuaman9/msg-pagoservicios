package pe.financiera.gw.pagoservicios.interbank.services.proxy.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;

import java.io.IOException;
import java.util.Optional;

@Component
public class InterbankErrorParser {

    private final ObjectMapper objectMapper;

    public InterbankErrorParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<InterbankApiException> parse(String errorBody) {
        return parse(errorBody, null);
    }

    public Optional<InterbankApiException> parse(String errorBody, HttpStatus ibkStatus) {
        if (errorBody == null || errorBody.isBlank()) {
            return Optional.empty();
        }

        try {
            InterbankErrorEnvelope envelope = objectMapper.readValue(errorBody, InterbankErrorEnvelope.class);
            if (envelope.getError() != null && hasBusinessData(envelope.getError())) {
                return Optional.of(InterbankApiException.fromIbkPayload(
                    envelope.getError().getCode(),
                    envelope.getError().getMessage(),
                    ibkStatus
                ));
            }

            InterbankErrorPayload payload = objectMapper.readValue(errorBody, InterbankErrorPayload.class);
            if (hasBusinessData(payload)) {
                return Optional.of(InterbankApiException.fromIbkPayload(
                    payload.getCode(),
                    payload.getMessage(),
                    ibkStatus
                ));
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private boolean hasBusinessData(InterbankErrorPayload payload) {
        return (payload.getCode() != null && !payload.getCode().isBlank())
            || (payload.getMessage() != null && !payload.getMessage().isBlank());
    }
}
