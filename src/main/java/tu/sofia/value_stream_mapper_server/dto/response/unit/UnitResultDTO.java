/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.unit;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnitResultDTO {
    @JsonProperty("units")
    List<UnitDTO> units;
}
