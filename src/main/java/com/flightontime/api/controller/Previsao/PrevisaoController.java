package com.flightontime.api.controller;

import com.flightontime.api.dto.PrevisaoRequest;
import com.flightontime.api.dto.PrevisaoResponse;
import com.flightontime.api.service.PrevisaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController

@RequestMapping("/api/previsao")
public class PrevisaoController {
    private final PrevisaoService previsaoService;


    public PrevisaoController(PrevisaoService previsaoService) {
        this.previsaoService = previsaoService;
    }

    /**
     * OPÇÃO 1: Entrada Manual
     */
    @PostMapping("/predict")
    public ResponseEntity<PrevisaoResponse> preverAtraso(@Valid @RequestBody PrevisaoRequest request) {
        // O Service já inclui a lógica de trânsito real dentro deste método
        PrevisaoResponse response = previsaoService.preverAtraso(request);
        return ResponseEntity.ok(response);
    }

    /**
     * OPÇÃO 2: Entrada Automática via Número do Voo (FlightAware)
     * O Backend recebe o código (ex: GLO1500) e busca os dados reais.
     */
    @GetMapping("/predict/flight/{ident}")
    public ResponseEntity<PrevisaoResponse> preverPorVooReal(@PathVariable String ident) {
        PrevisaoResponse response = previsaoService.preverAtrasoComVooReal(ident);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("api_status", "Online");
        status.put("service_name", "FlightOnTime Previsao API");
        status.put("current_time", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }
}