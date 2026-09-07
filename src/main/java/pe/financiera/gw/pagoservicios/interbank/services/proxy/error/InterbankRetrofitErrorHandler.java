package pe.financiera.gw.pagoservicios.interbank.services.proxy.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.util.exception.RestClientException;
import pe.financiera.gw.pagoservicios.util.exception.interbank.InterbankApiException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class InterbankRetrofitErrorHandler {

    private static final String LOG_PREFIX = "GW_SERVICE_PAY";

    private final InterbankErrorParser interbankErrorParser;

    public InterbankRetrofitErrorHandler(InterbankErrorParser interbankErrorParser) {
        this.interbankErrorParser = interbankErrorParser;
    }

    public void ensureSuccessful(Response<?> response, Class<?> source) throws IOException, RestClientException, InterbankApiException {
        if (response.isSuccessful()) {
            return;
        }

        String responseBody = readErrorBody(response);
        log.error("{}_ERROR_IBK_API: source={} httpStatus={} body={}",
            LOG_PREFIX, source.getSimpleName(), response.code(), responseBody);

        HttpStatus ibkStatus = Optional.ofNullable(HttpStatus.resolve(response.code()))
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        Optional<InterbankApiException> parsedError = interbankErrorParser.parse(responseBody, ibkStatus);
        if (parsedError.isPresent()) {
            throw parsedError.get();
        }

        throw new RestClientException(
            new IOException("Interbank API error"),
            source.getName(),
            responseBody,
            ibkStatus
        );
    }

    private String readErrorBody(Response<?> response) throws IOException {
        if (response.errorBody() == null) {
            return "";
        }
        return response.errorBody().string();
    }
}
