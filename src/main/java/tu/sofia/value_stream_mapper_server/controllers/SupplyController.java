/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.SupplyDTO;
import tu.sofia.value_stream_mapper_server.service.SupplyService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class SupplyController {
    @Autowired private SupplyService supplyService;

    @PutMapping(value = "/vsm/{id}/supply/supplier", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateSupplier(@PathVariable UUID id, @RequestBody SupplyDTO request) {
        try {
            var result = supplyService.updateSupply(id, request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping(value = "/vsm/{id}/supply/supplier", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createSupplier(@PathVariable UUID id, @RequestBody SupplyDTO request) {
        try {
            var result = supplyService.createSupplier(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping(
            value = "/vsm/{id}/supply/supplier/{supplierId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteSupplier(
            @PathVariable("id") UUID id, @PathVariable("supplierId") UUID supplierId) {
        try {
            var result = supplyService.deleteSupplier(id, supplierId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
