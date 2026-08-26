/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VSMObjectResultDTO {
    @JsonProperty("value_stream_map")
    private VSMObjectDTO objectDTO;
}
