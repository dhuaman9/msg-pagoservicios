package pe.financiera.gw.pagoservicios.util.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error con code y message")
public class ApiErrorResponse {

    private String code;

    private String message;

    private String description;

    public static ApiErrorResponse of(String code, String message) {
        return ApiErrorResponse.builder()
            .code(code)
            .message(message)
            .build();
    }

    public static ApiErrorResponse of(String code, String message, String description) {
        return ApiErrorResponse.builder()
            .code(code)
            .message(message)
            .description(description)
            .build();
    }
}
