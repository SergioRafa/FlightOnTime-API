package com.flightontime.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightontime.api.dto.ClimaResponseDTO;
import com.flightontime.api.dto.PrevisaoRequest;
import com.flightontime.api.dto.PrevisaoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PrevisaoService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final String mlApiUrl;
    private final double thresholdProb;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ClimaService climaService;
    private final NoticiasService noticiasService;

    public PrevisaoService(
            ClimaService climaService,
            NoticiasService noticiasService,
            @Value("${app.integration.ml.url}") String mlApiUrl,
            @Value("${app.integration.ml.threshold-prob}") double thresholdProb) {
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
        this.climaService = climaService;
        this.noticiasService = noticiasService;
        this.mlApiUrl = mlApiUrl;
        this.thresholdProb = thresholdProb;
    }

    public PrevisaoResponse preverAtraso(PrevisaoRequest request) {
        // 1. Busca Clima e Trânsito
        ClimaResponseDTO climaOrigem = climaService.buscarClima(request.getOrigem(), request.getDataPartida());
        boolean trafegoCriticoOrigem = noticiasService.buscarTransitoCritico(request.getOrigem());

        // 2. Prepara dados para IA
        Map<String, Object> featuresJson = preProcessar(request, climaOrigem, trafegoCriticoOrigem);

        // 3. Chama IA com Fallback (Se a IA falhar, o Java assume)
        Double probabilidade = chamarModeloML(featuresJson);

        // 4. Monta Resposta Completa
        PrevisaoResponse response = new PrevisaoResponse();
        response.setPrevisao(probabilidade >= this.thresholdProb ? "Atrasado" : "Pontual");
        response.setProbabilidade(probabilidade);
        response.setTemp_celsius(climaOrigem.getTemperaturaC());
        response.setVento_kmh(climaOrigem.getVelocidadeVentoKmH());

        return response;
    }

    private Double chamarModeloML(Map<String, Object> features) {
        try {
            String requestBody = objectMapper.writeValueAsString(features);
            String mlResponse = webClient.post()
                    .uri(this.mlApiUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(4)) // NÃO DEIXA TRAVAR
                    .block();

            Map<String, Object> responseMap = objectMapper.readValue(mlResponse, Map.class);
            return Double.valueOf(responseMap.get("probabilidade").toString());

        } catch (Exception e) {
            System.err.println("⚠️ Fallback ativado: Usando lógica interna do Java.");
            double risco = 0.15;
            if ((double)features.get("vento_kmh") > 20) risco += 0.20;
            if ((double)features.get("trafego_critico") == 1.0) risco += 0.35;
            return risco;
        }
    }

    private Map<String, Object> preProcessar(PrevisaoRequest request, ClimaResponseDTO clima, boolean trafegoCritico) {
        LocalDateTime dataHora = LocalDateTime.parse(request.getDataPartida(), FORMATTER);
        Map<String, Object> f = new HashMap<>();
        f.put("distancia_km", request.getDistanciaKm());
        f.put("hora_partida", (double)dataHora.getHour());
        f.put("dia_semana", (double)dataHora.getDayOfWeek().getValue());
        f.put("origem_GIG", request.getOrigem().equalsIgnoreCase("GIG") ? 1.0 : 0.0);
        f.put("temp_celsius", clima.getTemperaturaC());
        f.put("umidade_perc", clima.getUmidadeRelativa());
        f.put("vento_kmh", clima.getVelocidadeVentoKmH());
        f.put("trafego_critico", trafegoCritico ? 1.0 : 0.0);
        return f;
    }

    public PrevisaoResponse preverAtrasoComVooReal(String ident) {
        PrevisaoRequest mock = new PrevisaoRequest();
        mock.setOrigem("GIG");
        mock.setDestino("SSA");
        mock.setDataPartida(LocalDateTime.now().format(FORMATTER));
        mock.setDistanciaKm(800.0);
        return preverAtraso(mock);
    }
}