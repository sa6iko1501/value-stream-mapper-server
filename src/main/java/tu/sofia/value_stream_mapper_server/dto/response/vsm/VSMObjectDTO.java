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
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitDTO;

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

    @JsonProperty("shifts")
    private int shifts;

    @JsonProperty("length_of_shifts")
    private double lengthOfShifts;

    @JsonProperty("breaktime")
    private int breaktime;

    @JsonProperty("process_tables")
    private List<ProcessTableDTO> tables;

    @JsonProperty("supply")
    private SupplyDTO supply;

    @JsonProperty("unit")
    private UnitDTO unit;

    @JsonProperty("customer_output")
    private CustomerOutputDTO customerOutput;

    @JsonProperty("total_lead_time")
    private TotalLeadTimeDTO leadTime;
}
