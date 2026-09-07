package pe.financiera.gw.pagoservicios.util.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class JWTParser {
    private final ObjectMapper objectMapper;

    public JWTParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Value
    static public class Access {
        private final Long exp;

        public Access(@JsonProperty("exp") Long exp) {
            this.exp = exp;
        }
    }

    public Optional<Access> parse(String jwt) {
        String[] parts = jwt.split("\\.");
        Optional<Access> access = Optional.empty();
        if (parts.length == 3) {
            String body = parts[1];
            String decodedBody = new String(Base64.getDecoder().decode(body));
            try {
                access = Optional.of(
                    objectMapper.readValue(decodedBody, Access.class)
                );
            } catch (IOException exception) {
                log.error("Exception, ", exception);
            }
        } else {
            log.error("Invalid JWT token");
        }
        return access;
    }
}
