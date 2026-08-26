/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.service.ProcessTableService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ProcessTableController {
    @Autowired private ProcessTableService processTableService;

    @PostMapping("/table")
    public ResponseEntity<?> updateTable(@RequestBody ProcessTableDTO request) {
        var result = processTableService.updateProcessTable(request);
        return ResponseEntity.ok(null);
    }
}
