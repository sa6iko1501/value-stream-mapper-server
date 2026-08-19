/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SupplyDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("suppliers")
    private List<SupplierDTO> suppliers;
}
