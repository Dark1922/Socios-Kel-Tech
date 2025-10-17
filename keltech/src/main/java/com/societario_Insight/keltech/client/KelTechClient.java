package com.societario_Insight.keltech.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class KelTechClient {

    private final WebClient webClient;

    @Value("${app.keltech.base-url}")
    private String baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Retryable(maxAttemptsExpression = "${app.http.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.http.retry.backoff-ms}"))
    public JsonNode buscarQuadroSocietario() {
        String body = webClient.get()
                .uri(URI.create(baseUrl))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class)
                        .map(b -> new IllegalStateException("Erro: " + resp.statusCode() + " " + b)))
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Resposta da KelTech não é um JSON válido", e);
        }
    }

}
