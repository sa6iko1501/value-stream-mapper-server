/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventoryDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("nextInLine")
    private UUID nextInPosition;
}
