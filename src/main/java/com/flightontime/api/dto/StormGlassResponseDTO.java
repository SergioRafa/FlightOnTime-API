package com.flightontime.api.dto;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class StormGlassResponseDTO {

    @JsonProperty("hours")
    private List<WeatherData> hours;

    @Data
    public static class WeatherData {
        private String time;
        private ValueData airTemperature; // Temperatura do ar (em Celsius)
        private ValueData windSpeed;      // Velocidade do vento (em m/s)

        @Data
        public static class ValueData {
            private Double noaa; // Fonte de dados climaticos : valor fornecido pela NOAA
            // (National Oceanic and Atmospheric Administration).
        }
    }
}

