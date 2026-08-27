/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.service.ProcessTableService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ProcessTableController {
    @Autowired private ProcessTableService processTableService;

    @PutMapping("/table")
    public ResponseEntity<?> updateTable(@RequestBody ProcessTableDTO request) {
        try {
            var result = processTableService.updateProcessTable(request);
            return ResponseEntity.ok(result.getId());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/table")
    public ResponseEntity<?> createTable(@RequestBody ProcessTableDTO request) {
        try {
            var result = processTableService.createProcessTable(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/table/{id}")
    public ResponseEntity<?> deleteTable(@PathVariable UUID id) {
        try {
            var result = processTableService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
