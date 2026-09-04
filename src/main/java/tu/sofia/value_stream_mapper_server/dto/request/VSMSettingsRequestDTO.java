/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VSMSettingsRequestDTO {
    @JsonProperty("shifts")
    private int shifts;

    @JsonProperty("length_of_shifts")
    private double lengthOfShifts;

    @JsonProperty("breaktime")
    private int breaktime;
}
