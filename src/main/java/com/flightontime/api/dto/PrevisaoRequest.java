package com.flightontime.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Data
public class PrevisaoRequest {

    @NotBlank(message = "A companhia aérea é obrigatória.")
    private String companhia;

    @NotBlank(message = "O aeroporto de origem é obrigatório.")
    private String origem;

    @NotBlank(message = "O aeroporto de destino é obrigatório.")
    private String destino;

    @NotBlank(message = "A data e hora de partida são obrigatórias e devem estar no formato ISO.")
    private String dataPartida;

    @NotNull(message = "A distância em KM é obrigatória.")
    private Double distanciaKm;
}