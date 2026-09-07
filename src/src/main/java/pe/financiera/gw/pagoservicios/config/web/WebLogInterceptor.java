package pe.financiera.gw.pagoservicios.config.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.financiera.gw.pagoservicios.util.logger.WebLogger;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
public class WebLogInterceptor implements HandlerInterceptor {

    private WebLogger webLogger;

    public WebLogInterceptor(WebLogger webLogger) {
        this.webLogger = webLogger;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
            && request.getMethod().equals(HttpMethod.GET.name())
            && log.isDebugEnabled()) {
            webLogger.logRequest(request, null);
        }
        return true;
    }
}
