/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VSMDashboardResultDTO {
    @JsonProperty("vsm_for_user")
    public List<VSMDashboardResultObjectDTO> objectDTOs;
}
