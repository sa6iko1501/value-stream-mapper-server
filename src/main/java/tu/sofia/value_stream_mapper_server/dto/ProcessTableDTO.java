/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessTableDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("inventory")
    private InventoryDTO inventory;

    @JsonProperty("rows")
    private List<TableRowDTO> rows;

    @JsonProperty("title")
    private String title;
}
