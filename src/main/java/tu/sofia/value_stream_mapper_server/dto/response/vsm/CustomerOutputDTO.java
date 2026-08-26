/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dto.response.vsm;

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
public class CustomerOutputDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("customers")
    private List<CustomerDTO> customers;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("unit")
    private String unit;
}
