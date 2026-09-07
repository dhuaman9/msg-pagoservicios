package pe.financiera.gw.pagoservicios.util.logger;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebLoggerTest {

    private WebLogger webLogger;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    public void setUp() {
        webLogger = new WebLogger();
    }

    @Test
    public void testLogRequest_ShouldLogWebRequest_WhenRequestHasParametersAndBody() {
        Object object = new Object();
        String key = "identifier";
        String value = "123123";
        Enumeration<String> parameters = mock(Enumeration.class);
        Enumeration<String> headers = mock(Enumeration.class);
        when(parameters.hasMoreElements()).thenReturn(true).thenReturn(false);
        when(parameters.nextElement()).thenReturn(key);
        when(httpServletRequest.getParameter(eq(key))).thenReturn(value);
        when(httpServletRequest.getParameterNames()).thenReturn(parameters);
        when(headers.hasMoreElements()).thenReturn(true).thenReturn(false);
        when(headers.nextElement()).thenReturn(key);
        when(httpServletRequest.getHeaderNames()).thenReturn(headers);
        webLogger.logRequest(httpServletRequest, object);
        verify(httpServletRequest, times(1)).getParameterNames();
        verify(httpServletRequest, times(1)).getHeaderNames();
    }

    @Test
    public void testLogRequest_ShouldLogWebRequest_WhenRequestHasNotParametersAndBody() {
        Enumeration<String> parameters = mock(Enumeration.class);
        Enumeration<String> headers = mock(Enumeration.class);
        when(parameters.hasMoreElements()).thenReturn(false);
        when(httpServletRequest.getParameterNames()).thenReturn(parameters);
        when(headers.hasMoreElements()).thenReturn(false);
        when(httpServletRequest.getHeaderNames()).thenReturn(headers);
        webLogger.logRequest(httpServletRequest, null);
        verify(httpServletRequest, times(1)).getParameterNames();
        verify(httpServletRequest, times(1)).getHeaderNames();
    }

    @Test
    public void testLogResponse_ShouldLogWebResponse_WhenResponseHasHeaders() {
        String header = "device";
        Object object = new Object();
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/customers/profile");
        when(httpServletResponse.getStatus()).thenReturn(200);
        Collection<String> headers = new ArrayList<>();
        headers.add(header);
        when(httpServletResponse.getHeader(eq(header))).thenReturn("Android");
        when(httpServletResponse.getHeaderNames()).thenReturn(headers);
        webLogger.logResponse(httpServletRequest, httpServletResponse, object);
        verify(httpServletResponse, times(1)).getHeaderNames();
    }

    @Test
    public void testLogResponse_ShouldLogWebResponse_WhenResponseHasNotHeaders() {
        Object object = new Object();
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(httpServletRequest.getRequestURI()).thenReturn("/customers/profile");
        when(httpServletResponse.getStatus()).thenReturn(200);
        Collection<String> headers = new ArrayList<>();
        when(httpServletResponse.getHeaderNames()).thenReturn(headers);
        webLogger.logResponse(httpServletRequest, httpServletResponse, object);
        verify(httpServletResponse, times(1)).getHeaderNames();
    }
}
