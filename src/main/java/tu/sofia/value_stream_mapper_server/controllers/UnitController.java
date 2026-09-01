/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.service.UnitService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UnitController {
    @Autowired private UnitService unitService;

    @GetMapping(value = "/unit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getAllUnits() {
        try {
            var resultDTO = unitService.getAllUnitsAsDTO();
            return ResponseEntity.ok(resultDTO);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
