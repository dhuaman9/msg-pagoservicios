package pe.financiera.gw.pagoservicios.util.logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebLog {

    private String marker;

    private String type;

    private String requestId;

    private String timestamp;

    private String method;

    private Integer status;

    private String path;

    private Object headers;

    private Object parameters;

    private Object body;

}
