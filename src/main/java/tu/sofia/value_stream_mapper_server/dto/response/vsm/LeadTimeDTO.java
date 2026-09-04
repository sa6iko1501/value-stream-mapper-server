package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LeadTimeDTO {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("matching_element")
    private UUID matchingElement;
    @JsonProperty("value")
    private double value;
    @JsonProperty("position")
    private int position;
}
