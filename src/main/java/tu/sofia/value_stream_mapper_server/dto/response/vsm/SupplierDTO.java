/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("unit_name")
    private String unitName;

    @JsonProperty("daily_supply")
    private Double dailySupply;
}
