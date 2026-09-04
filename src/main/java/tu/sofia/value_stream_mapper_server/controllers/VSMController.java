/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.request.VSMSettingsRequestDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMDashboardResultDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.service.ProcessTableService;
import tu.sofia.value_stream_mapper_server.service.VSMService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class VSMController {
    @Autowired private final VSMService vsmService;
    @Autowired private final ProcessTableService tableService;

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

    @PutMapping(value = "/vsm/{id}/positioning", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updatePositioning(
            @PathVariable UUID id, @RequestBody List<ProcessTableDTO> request) {
        try {
            var result = tableService.updatePositioning(id, request);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping(value = "/vsm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateVSMTitle(
            @PathVariable UUID id, @RequestParam(value = "title") String title) {
        try {
            var result = vsmService.updateVSMTitle(id, title);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping(value = "/vsm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createVSM(@RequestBody VSMObjectDTO request) {
        try {
            var result = vsmService.createVsmObj(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping(value = "/vsm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> deleteVsmObject(@PathVariable UUID id) {
        try {
            var result = vsmService.deleteVSM(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping(value = "/vsm/{id}/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateVsmSettings(
            @PathVariable UUID id, @RequestBody VSMSettingsRequestDTO request) {
        try {
            var result = vsmService.updateSettings(id, request);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
