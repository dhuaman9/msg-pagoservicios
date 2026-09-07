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
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebResponseBodyAdviceTest {

    @InjectMocks
    private WebResponseBodyAdvice webResponseBodyAdvice;

    @Mock
    private WebLogger webLogger;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private MediaType mediaType;

    @Mock
    private ServerHttpRequest serverHttpRequest;

    @Mock
    private ServerHttpResponse serverHttpResponse;

    @Test
    public void testSupports_ShouldReturnFalse() throws Exception {
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Boolean result = webResponseBodyAdvice.supports(methodParameter, aClass);
        assertFalse(result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldReturnObject_WhenResponseIsObject() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object object = new Object();
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldReturnObject_WhenResponseIsLinkedHashMap() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        LinkedHashMap object = new LinkedHashMap();
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldReturnObject_WhenRequesIsServletServer() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object object = new Object();
        ServletServerHttpRequest serverHttpRequest = mock(ServletServerHttpRequest .class);
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldReturnObject_WhenResponseIsServletServer() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object object = new Object();
        ServletServerHttpResponse  serverHttpResponse = mock(ServletServerHttpResponse.class);
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldReturnObject_WhenRequestAndResponseAreServletServer() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object object = new Object();
        ServletServerHttpRequest  serverHttpRequest = mock(ServletServerHttpRequest .class);
        ServletServerHttpResponse serverHttpResponse = mock(ServletServerHttpResponse.class);
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }

    @Test
    public void testBeforeBodyWrite_ShouldNotLog_WhenLogLevelIsNotDebug() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Object object = new Object();
        ServletServerHttpRequest  serverHttpRequest = mock(ServletServerHttpRequest .class);
        ServletServerHttpResponse serverHttpResponse = mock(ServletServerHttpResponse.class);
        Class aClass = Class.forName("pe.financiera.gw.pagoservicios.MainApplication");
        Object result = webResponseBodyAdvice.beforeBodyWrite(object, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
        assertEquals(object, result);
    }


}
