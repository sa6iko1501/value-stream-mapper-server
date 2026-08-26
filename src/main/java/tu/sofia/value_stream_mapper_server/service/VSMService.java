/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMDashboardResultDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.models.VSMObject;
import tu.sofia.value_stream_mapper_server.service.utils.VSMToDTOMapper;
import tu.sofia.value_stream_mapper_server.service.utils.VSMToIDAndDateMapper;

@Service
@AllArgsConstructor
public class VSMService {
    @Autowired private final VSMObjectDAO vsmRepo;
    @Autowired private final VSMToIDAndDateMapper vsmToIDAndDateMapper;
    @Autowired private final VSMToDTOMapper vsmToDTOMapper;

    public VSMDashboardResultDTO findAllSavedVSMIds() {
        List<VSMObject> allVSM = vsmRepo.findAll();

        return vsmToIDAndDateMapper.toDTO(allVSM);
    }

    public VSMObjectResultDTO findById(UUID id) {
        Optional<VSMObject> vsmOptional = vsmRepo.findById(id);
        if (vsmOptional.isPresent()) {
            return vsmToDTOMapper.toDTO(vsmOptional.get());
        }
        throw new IllegalArgumentException("The requested vsm was not found");
    }
}
