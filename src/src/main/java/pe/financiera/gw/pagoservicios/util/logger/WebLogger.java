package pe.financiera.gw.pagoservicios.util.logger;

import com.google.gson.Gson;
import org.slf4j.*;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

import static net.logstash.logback.marker.Markers.append;
import static pe.financiera.gw.pagoservicios.util.constants.LoggerConstants.*;

@Component
public class WebLogger {

    private static final Gson GSON = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogger.class);

    private static final Marker WEB_REQUEST_LEVEL = MarkerFactory.getMarker(WEB_REQUEST);

    private static final Marker WEB_RESPONSE_LEVEL = MarkerFactory.getMarker(WEB_RESPONSE);

    private static final String REQUEST_ID = "RequestId";

    private WebLog webLog;

    private static void debug(Marker marker, WebLog eventLog, String webTagLog){
        LOGGER.debug(marker, MESSAGE_LOG, GSON.toJson(eventLog), append(webTagLog, eventLog));
    }

    public void logRequest(HttpServletRequest httpServletRequest, Object body) {
        Map<String, String> parameters = buildParametersMap(httpServletRequest);
        webLog = WebLog.builder().build();
        webLog.setRequestId(UUID.randomUUID().toString());
        webLog.setTimestamp(DATE_FORMAT_LOG.format(new Date()));
        webLog.setMethod(httpServletRequest.getMethod());
        webLog.setPath(httpServletRequest.getRequestURI());
        webLog.setHeaders(buildHeadersMap(httpServletRequest));
        webLog.setMarker(WEB_REQUEST_LEVEL.getName());
        webLog.setType(WEB_REQUEST_LEVEL.getName());

        if (!parameters.isEmpty()) {
            webLog.setParameters(parameters);
        }

        if (body != null) {
            webLog.setBody(body);
        }


        MDC.put(REQUEST_ID, webLog.getRequestId());
        debug(WEB_REQUEST_LEVEL, webLog, REQUEST_TAG_LOG);

    }

    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        webLog = WebLog.builder().build();
        webLog.setRequestId(MDC.get(REQUEST_ID));
        webLog.setTimestamp(DATE_FORMAT_LOG.format(new Date()));
        webLog.setMarker(WEB_RESPONSE_LEVEL.getName());
        webLog.setType(WEB_RESPONSE_LEVEL.getName());
        webLog.setMethod(httpServletRequest.getMethod());
        webLog.setPath(httpServletRequest.getRequestURI());
        webLog.setHeaders(buildHeadersMap(httpServletResponse));
        webLog.setStatus(httpServletResponse.getStatus());
        webLog.setBody(body);
        debug(WEB_RESPONSE_LEVEL, webLog, RESPONSE_TAG_LOG);
        MDC.clear();
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }



}
