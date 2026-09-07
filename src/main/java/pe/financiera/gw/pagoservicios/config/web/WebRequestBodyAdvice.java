package pe.financiera.gw.pagoservicios.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

@RestControllerAdvice
@Slf4j
public class WebRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private WebLogger webLogger;

    private HttpServletRequest httpServletRequest;

    @Autowired
    public WebRequestBodyAdvice(WebLogger webLogger, HttpServletRequest httpServletRequest) {
        this.webLogger = webLogger;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return false;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        if(log.isDebugEnabled()) {
            webLogger.logRequest(httpServletRequest, body);
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

}
