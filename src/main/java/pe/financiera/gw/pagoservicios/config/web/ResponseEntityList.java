package pe.financiera.gw.pagoservicios.config.web;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEntityList<T> {

    @JsonProperty("metadata")
    private MetadataProperties metadata;

    @JsonProperty("result")
    private List<T> result;

}
