/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VSMObjectDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("date")
    private LocalDateTime date;

    @JsonProperty("process_tables")
    private List<ProcessTableDTO> tables;

    @JsonProperty("supply")
    private SupplyDTO supply;

    @JsonProperty("customer_output")
    private CustomerOutputDTO customerOutput;
}
