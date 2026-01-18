package com.flightontime.api.service;

import com.flightontime.api.dto.ClimaResponseDTO;
import com.flightontime.api.dto.StormGlassResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ClimaService {

    // ALTERE PARA 'false' APENAS NA HORA DA APRESENTAÇÃO OFICIAL SE QUISER USAR A API REAL
    private static final boolean MODO_DEMO = true;

    @Value("${app.integration.clima.url}")
    private String climaApiUrl;

    @Value("${app.integration.clima.key}")
    private String climaApiKey;

    @Value("${app.integration.clima.coords.gig-lat}")
    private double gigLat;

    @Value("${app.integration.clima.coords.gig-lon}")
    private double gigLon;

    private final WebClient webClient;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ClimaService() {
        this.webClient = WebClient.create();
    }

    public ClimaResponseDTO buscarClima(String codigoIata, String dataHora) {
        double lat;
        double lon;
        String iata = codigoIata.toUpperCase();

        // 1. Seleção de Coordenadas
        switch (iata) {
            case "GIG": lat = gigLat; lon = gigLon; break;
            case "GRU": lat = -23.4356; lon = -46.4731; break;
            case "CGH": lat = -23.6273; lon = -46.6565; break;
            case "BSB": lat = -15.8692; lon = -47.9172; break;
            case "SSA": lat = -12.9086; lon = -38.3225; break;
            case "REC": lat = -8.1264; lon = -34.9228; break;
            case "FOR": lat = -3.7763; lon = -38.5326; break;
            case "CWB": lat = -25.5317; lon = -49.1744; break;
            case "POA": lat = -29.9939; lon = -51.1711; break;
            case "MAO": lat = -3.0357; lon = -60.0506; break;
            default: lat = -23.4356; lon = -46.4731; break;
        }

        // 2. MODO DEMO: Economiza API StormGlass com valores realistas
        if (MODO_DEMO) {
            return gerarClimaSimulado(iata);
        }

        // 3. Chamada Real para StormGlass
        String isoTime;
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dataHora, ISO_FORMATTER);
            isoTime = parsedDateTime.format(ISO_FORMATTER) + "Z";
        } catch (Exception e) {
            isoTime = LocalDateTime.now().format(ISO_FORMATTER) + "Z";
        }

        try {
            String uri = String.format(Locale.US, "%s?lat=%.2f&lng=%.2f&params=airTemperature,windSpeed&start=%s&end=%s",
                    climaApiUrl, lat, lon, isoTime, isoTime);

            System.out.println("Chamando StormGlass REAL: " + uri);

            StormGlassResponseDTO apiResponse = webClient.get()
                    .uri(uri)
                    .header("Authorization", climaApiKey)
                    .retrieve()
                    .bodyToMono(StormGlassResponseDTO.class)
                    .block();

            if (apiResponse != null && apiResponse.getHours() != null && !apiResponse.getHours().isEmpty()) {
                StormGlassResponseDTO.WeatherData data = apiResponse.getHours().get(0);
                double temp = data.getAirTemperature().getNoaa();
                double wind = data.getWindSpeed().getNoaa() * 3.6;
                System.out.printf(Locale.US, "Dados REAIS: Temp=%.1f°C, Vento=%.1f km/h%n", temp, wind);
                return new ClimaResponseDTO(temp, 0.0, "Real", wind);
            }
            return new ClimaResponseDTO(25.0, 60.0, "Fallback", 10.0);

        } catch (Exception e) {
            System.err.println("ERRO StormGlass: " + e.getMessage());
            return gerarClimaSimulado(iata); // Se falhar a API, usa o simulado
        }
    }

    private ClimaResponseDTO gerarClimaSimulado(String iata) {
        double temp, wind;
        switch (iata) {
            case "MAO": temp = 31.2; wind = 4.5; break;
            case "CWB": temp = 17.5; wind = 14.2; break;
            case "SSA": temp = 28.8; wind = 19.5; break;
            case "POA": temp = 21.3; wind = 11.8; break;
            default: temp = 24.5; wind = 8.0; break;
        }
        System.out.println("MODO DEMO: Usando dados simulados para " + iata);
        return new ClimaResponseDTO(temp, 0.0, "Simulado", wind);
    }
}