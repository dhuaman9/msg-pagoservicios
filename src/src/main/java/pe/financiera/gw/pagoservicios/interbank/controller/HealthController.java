package pe.financiera.gw.pagoservicios.interbank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Void> rootHealthCheck() {
        return ResponseEntity.ok().build();
    }
}
