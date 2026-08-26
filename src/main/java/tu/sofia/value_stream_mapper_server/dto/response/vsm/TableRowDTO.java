/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitDTO;
import tu.sofia.value_stream_mapper_server.models.RowType;

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
    private UnitDTO unit;

    @JsonProperty("row_type")
    private RowType rowType;

    @JsonProperty("value")
    private Double value;
}
