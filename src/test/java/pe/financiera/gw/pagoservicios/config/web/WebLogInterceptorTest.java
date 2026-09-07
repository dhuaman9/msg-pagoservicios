package pe.financiera.gw.pagoservicios.config.web;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.ModelAndView;

import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebLogInterceptorTest {

    @InjectMocks
    private WebLogInterceptor webLogInterceptor;

    @Mock
    private WebLogger webLogger;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ModelAndView modelAndView;

    private String userId = "091a74e5-7710-4b1f-a93b-edf5c95ab0a4";

    private String token = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjA5MWE3NGU1LTc3MTAtNGIxZi1hOTNiLWVkZjVjOTVhYjBhNCIsInR5cGUiOiJBQ0NFU1MiLCJleHAiOjE1NjcwMTI5Nzh9.XhFdX4SprHjZQVujd4WXifcvlMUEjq19VqBB7yi-LBN8beDm0pk2REnZZnzSqsc4v184gI8rij0tPIYVk5q15A";

    @Test
    public void testPreHandle_ShouldLog_WhenInterceptorIsForRequestAndGetMethod() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object handler = new Object();
        doNothing().when(webLogger).logRequest(eq(httpServletRequest), any());
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        webLogInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);
        verify(webLogger, times(1)).logRequest(eq(httpServletRequest), any());
    }

    @Test
    public void testPreHandle_ShouldLog_WhenInterceptorIsNotForError() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object handler = new Object();
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.ERROR);
        webLogInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);
        verify(webLogger, times(0)).logRequest(eq(httpServletRequest), any());
    }

    @Test
    public void testPreHandle_ShouldLog_WhenInterceptorIsForRequestAndPostMethod() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        Object handler = new Object();
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.POST.name());
        webLogInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);
        verify(webLogger, times(0)).logRequest(eq(httpServletRequest), any());
    }

    @Test
    public void testPreHandle_ShouldNotLog_WhenLogLevelIsNotDebug() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Object handler = new Object();
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.name());
        webLogInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);
        verify(webLogger, times(0)).logRequest(eq(httpServletRequest), any());
    }
}
