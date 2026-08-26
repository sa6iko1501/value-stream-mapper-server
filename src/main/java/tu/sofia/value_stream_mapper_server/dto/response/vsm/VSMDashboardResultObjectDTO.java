/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VSMDashboardResultObjectDTO {
    @JsonProperty("id")
    public UUID id;

    @JsonProperty("title")
    public String title;

    @JsonProperty("date")
    public LocalDateTime date;
}
