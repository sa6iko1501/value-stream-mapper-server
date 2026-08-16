/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dto.VSMDashboardResultDTO;
import tu.sofia.value_stream_mapper_server.dto.VSMDashboardResultObjectDTO;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
@NoArgsConstructor
public class VSMToIDAndDateMapper {
    public VSMDashboardResultDTO toDTO(List<VSMObject> objects) {
        List<VSMDashboardResultObjectDTO> resultObjectDTOList = new ArrayList<>();
        for (VSMObject vsm : objects) {
            resultObjectDTOList.add(
                    new VSMDashboardResultObjectDTO(vsm.getId(), vsm.getTitle(), vsm.getDate()));
        }

        return new VSMDashboardResultDTO(resultObjectDTOList);
    }
}
