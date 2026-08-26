/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service.utils;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitDTO;
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitResultDTO;
import tu.sofia.value_stream_mapper_server.models.Unit;

@Service
@AllArgsConstructor
public class UnitToDTOMapper {
    public UnitResultDTO toDTO(List<Unit> units) {
        List<UnitDTO> unitDTOList =
                units.stream()
                        .map(
                                x -> {
                                    return new UnitDTO(x.getId(), x.getName(), x.getUnitType());
                                })
                        .toList();
        return new UnitResultDTO(unitDTOList);
    }
}
