/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TableRowDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("row_name")
    private String rowName;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("value")
    private Double value;
}
