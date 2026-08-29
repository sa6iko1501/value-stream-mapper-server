/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import java.util.UUID;
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
import org.springframework.web.bind.annotation.RestController;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.CustomerOutputDTO;
import tu.sofia.value_stream_mapper_server.service.CustomerService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {
    @Autowired private CustomerService customerService;

    @PutMapping(value = "/vsm/{id}/customer-output/customer")
    public ResponseEntity<?> updateCustomerOutput(
            @PathVariable UUID id, @RequestBody CustomerOutputDTO request) {
        try {
            var result = customerService.updateCustomerOutput(id, request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping(value = "/vsm/{id}/customer-output/customer")
    public ResponseEntity<?> createCustomer(
            @PathVariable UUID id, @RequestBody CustomerOutputDTO request) {
        try {
            var result = customerService.createCustomer(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping(
            value = "/vsm/{id}/customer-output/customer/{customerId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id, @PathVariable UUID customerId) {
        try {
            var result = customerService.deleteCustomer(id, customerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
