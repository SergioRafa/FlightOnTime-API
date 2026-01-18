package com.flightontime.api.dto;

public class PrevisaoResponse {
    private String previsao;
    private Double probabilidade;
    private Double temp_celsius;
    private Double vento_kmh;
    private String status_processamento;

    // Getters e Setters
    public String getPrevisao() { return previsao; }
    public void setPrevisao(String previsao) { this.previsao = previsao; }

    public Double getProbabilidade() { return probabilidade; }
    public void setProbabilidade(Double probabilidade) { this.probabilidade = probabilidade; }

    public Double getTemp_celsius() { return temp_celsius; }
    public void setTemp_celsius(Double temp_celsius) { this.temp_celsius = temp_celsius; }

    public Double getVento_kmh() { return vento_kmh; }
    public void setVento_kmh(Double vento_kmh) { this.vento_kmh = vento_kmh; }

    public String getStatus_processamento() { return status_processamento; }
    public void setStatus_processamento(String status_processamento) { this.status_processamento = status_processamento; }
}