/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.UnitDAO;
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitResultDTO;
import tu.sofia.value_stream_mapper_server.models.Unit;
import tu.sofia.value_stream_mapper_server.service.utils.UnitToDTOMapper;

@Service
@AllArgsConstructor
public class UnitService {
    @Autowired private UnitDAO unitRepo;
    @Autowired private UnitToDTOMapper mapper;

    public UnitResultDTO getAllUnitsAsDTO() {
        var units = getAllUnits();
        if (units.isEmpty()) {
            throw new IllegalStateException("No units in app.");
        }

        return mapper.toDTO(units);
    }

    public List<Unit> getAllUnits() {
        return unitRepo.findAll(Sort.by(Sort.Order.by("name")));
    }
}
