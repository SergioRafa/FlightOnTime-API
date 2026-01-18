package com.flightontime.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Útil para simulação
public class ClimaResponseDTO {
    // Features relevantes que serão enviadas ao ML
    private double temperaturaC;
    private double umidadeRelativa;
    private String condicao; // Ex: "Chuva", "Limpo"
    private double velocidadeVentoKmH;
}

