package pe.financiera.gw.pagoservicios.util.authorization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class JWTParserTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final JWTParser underTest = new JWTParser(objectMapper);

    @Test
    public void parseOk() throws IOException {
        // given
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJ0b2tlbjphcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uOjRZekYzZEdiSmdWdEU2eW4iXSwiZXhwIjoxNTY4OTA4MTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1RSVVNURURfQ0xJRU5UIiwiUk9MRV9DTElFTlQiXSwianRpIjoiNjcyZjU1NGQtNDM2ZC00ZTQ3LWEzZTAtZGRhM2YzZGNiNzZhIiwiY2xpZW50X2lkIjoiNFl6RjNkR2JKZ1Z0RTZ5biJ9.p00xpRwMC0jWqIOytnKzGy1QtvbH_Qz9FdJDuaAAyyE";
        // decoded in jwt.io
        Long exp = 1568908159L;
        // then
        Optional<JWTParser.Access> optionalAccess = underTest.parse(jwt);
        // assert
        assertTrue(optionalAccess.isPresent());
        assertEquals(exp, optionalAccess.get().getExp());
    }

    @Test
    public void invalidLength() {
        // given
        String jwt = "eyJzY29wZSI6WyJ0b2tlbjphcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uOjRZekYzZEdiSmdWdEU2eW4iXSwiZXhwIjoxNTY4OTA4MTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1RSVVNURURfQ0xJRU5UIiwiUk9MRV9DTElFTlQiXSwianRpIjoiNjcyZjU1NGQtNDM2ZC00ZTQ3LWEzZTAtZGRhM2YzZGNiNzZhIiwiY2xpZW50X2lkIjoiNFl6RjNkR2JKZ1Z0RTZ5biJ9.p00xpRwMC0jWqIOytnKzGy1QtvbH_Qz9FdJDuaAAyyE";
        // then
        Optional<JWTParser.Access> optionalAccess = underTest.parse(jwt);
        // assert
        assertFalse(optionalAccess.isPresent());
    }

//    @Test
    public void invalidJson() {
        // given
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZI6WyJ0b2tlbjphcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uOjRZekYzZEdiSmdWdEU2eW4iXSwiZXhwIjoxNTY4OTA4MTU5LCJhdXRob3JpdGllcyI6WyJST0xFX1RSVVNURURfQ0xJRU5UIiwiUk9MRV9DTElFTlQiXSwianRpIjoiNjcyZjU1NGQtNDM2ZC00ZTQ3LWEzZTAtZGRhM2YzZGNiNzZhIiwiY2xpZW50X2lkIjoiNFl6RjNkR2JKZ1Z0RTZ5biJ9.p00xpRwMC0jWqIOytnKzGy1QtvbH_Qz9FdJDuaAAyyE";
        // then
        Optional<JWTParser.Access> optionalAccess = underTest.parse(jwt);
        // assert
        assertFalse(optionalAccess.isPresent());
    }
}
