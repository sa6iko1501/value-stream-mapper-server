/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class VSMDashboardController {
    @GetMapping(value = "/vsm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVsmIds() {
        Resource resource = new ClassPathResource("vsm_for_user.json");

        return ResponseEntity.ok(resource);
    }

    @GetMapping(value = "/vsm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getVsmById(@PathVariable int id) {
        Resource resource = null;
        if (id == 1) {
            resource = new ClassPathResource("request.json");
        }

        return ResponseEntity.ok(resource);
    }
}
