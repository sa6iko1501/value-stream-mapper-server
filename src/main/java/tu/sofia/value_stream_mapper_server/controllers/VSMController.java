/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMDashboardResultDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.service.VSMService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class VSMController {
    @Autowired private final VSMService vsmService;

    @GetMapping(value = "/vsm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VSMDashboardResultDTO> getVsmIds() {
        var result = vsmService.findAllSavedVSMIds();

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/vsm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getVsmById(@PathVariable UUID id) {
        try {
            VSMObjectResultDTO resultDTO = vsmService.findById(id);
            return ResponseEntity.ok(resultDTO);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
