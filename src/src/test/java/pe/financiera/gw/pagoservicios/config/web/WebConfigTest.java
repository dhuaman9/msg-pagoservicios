package pe.financiera.gw.pagoservicios.config.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebConfigTest {

    @InjectMocks
    private WebConfig webConfig;

    @InjectMocks
    private ObjectMapperConfig objectMapperConfig;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Test
    public void testAddInterceptors_ShouldAddInterceptor() {
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(interceptorRegistry.addInterceptor(any(HandlerInterceptor.class))).thenReturn(registration);
        webConfig.addInterceptors(interceptorRegistry);
        verify(interceptorRegistry, times(1)).addInterceptor(any(HandlerInterceptor.class));
    }

    @Test
    public void testObjectMapper_ShouldReturnCustomerObjectMapper() {
        assertNotNull(objectMapperConfig.objectMapper());
    }}
