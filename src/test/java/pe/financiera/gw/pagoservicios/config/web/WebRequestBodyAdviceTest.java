package pe.financiera.gw.pagoservicios.config.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class WebRequestBodyAdviceTest {

    @InjectMocks
    private WebRequestBodyAdvice webRequestBodyAdvice;

    @Mock
    private WebLogger webLogger;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private HttpInputMessage inputMessage;

    @Mock
    private Type targetType;

    @Test
    public void testSupports_ShouldReturnFalse() throws Exception {
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Boolean result = webRequestBodyAdvice.supports(methodParameter, targetType, aClass);
        assertFalse(result);
    }

    @Test
    public void testAfterBodyRead_ShouldReturnObject_WhenResponseIsObject() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object object = new Object();
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        doNothing().when(webLogger).logRequest(eq(httpServletRequest), eq(object));
        Object result = webRequestBodyAdvice.afterBodyRead(object, inputMessage, methodParameter, targetType, aClass);
        assertEquals(object, result);
    }

    @Test
    public void testAfterBodyRead_ShouldNotLog_WhenLogLevelIsNotDebug() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Object object = new Object();
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webRequestBodyAdvice.afterBodyRead(object, inputMessage, methodParameter, targetType, aClass);
        assertEquals(object, result);
    }
}
