package com.societario_Insight.keltech.client;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class CnpjReceitaClient {
    private final WebClient webClient;

    @Value("${app.receita.base-url}")
    private String baseUrl;

    @Retryable(maxAttemptsExpression = "${app.http.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${app.http.retry.backoff-ms}"))
    public JsonNode buscarPorCnpj(String cnpjSomenteDigitos) {
        String url = String.format("%s/%s", baseUrl, cnpjSomenteDigitos);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class)
                        .map(body -> new IllegalStateException("Receita erro: " + resp.statusCode() + " " + body)))
                .bodyToMono(JsonNode.class)
                .block();
    }
}
