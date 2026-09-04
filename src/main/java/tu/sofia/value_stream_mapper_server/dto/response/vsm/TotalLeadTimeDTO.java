package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TotalLeadTimeDTO {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("lead_time")
    private double leadTime;
    @JsonProperty("value_added")
    private double valueAdded;
    @JsonProperty("lead_times")
    private List<LeadTimeDTO> leadTimes;
}
