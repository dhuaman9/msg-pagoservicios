package pe.financiera.gw.pagoservicios.util.constants;

import java.text.SimpleDateFormat;

public final class LoggerConstants {

    public static final SimpleDateFormat DATE_FORMAT_LOG = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final String EVENT_TAG_LOG = "event";

    public static final String SERVICE_TAG_LOG = "service";

    public static final String REQUEST_TAG_LOG = "request";

    public static final String RESPONSE_TAG_LOG = "response";

    public static final String BUSINESS_MARKER_LOG = "BUSINESS";

    public static final String SERVICE_MARKER_LOG = "SERVICE_";

    public static final String SERVICE_SEND = "SEND";

    public static final String SERVICE_RECEIVE = "RECEIVE";

    public static final String SERVICE_ERROR = "ERROR";

    public static final String WEB_REQUEST = "REQUEST";

    public static final String WEB_RESPONSE = "RESPONSE";

    public static final String MESSAGE_LOG = "{}";
}
