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
public class SupplyInventoryDTO {
    @JsonProperty("supply_inventory")
    private double supplyInventory;
}
