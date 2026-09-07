package pe.financiera.gw.pagoservicios.config.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataProperties {

    @JsonProperty("count")
    private Long count;

    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("pageNumber")
    private Integer pageNumber;

}
