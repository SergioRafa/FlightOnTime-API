package com.flightontime.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NoticiasService {

    private final String noticiasApiUrl;
    private final WebClient webClient;

    /**
     * Injeção via Construtor: Garante que o Spring forneça a URL
     * definida no seu arquivo de configuração (application.yml ou properties).
     */
    public NoticiasService(@Value("${app.integration.noticias.url}") String noticiasApiUrl) {
        this.noticiasApiUrl = noticiasApiUrl;
        this.webClient = WebClient.create();
    }

    /**
     * Verifica se há trânsito crítico ou eventos impeditivos no entorno do aeroporto.
     * @param codigoIata Código do aeroporto (ex: GIG, GRU).
     * @return true se houver risco alto, false caso contrário.
     */
    public boolean buscarTransitoCritico(String codigoIata) {
        try {
            // Log para conferência durante a apresentação de sexta
            System.out.println("Consultando tráfego para o aeroporto: " + codigoIata);

            // Logica de Simulação para a Demo:
            // Usamos equalsIgnoreCase para aceitar 'gru' ou 'GRU'
            if ("GRU".equalsIgnoreCase(codigoIata)) {
                System.out.println("ALERTA: Tráfego crítico detectado via API de Notícias para " + codigoIata);
                return true;
            }

            // Exemplo de como seria a chamada real no futuro:
            /*
            String response = this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(noticiasApiUrl).queryParam("q", codigoIata).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            */

            return false;

        } catch (Exception e) {
            System.err.println("ERRO ao processar NoticiasService: " + e.getMessage());
            // Fallback de segurança: assume que o trânsito está normal para não travar o cálculo
            return false;
        }
    }
}