package xyz.firestige.qcare.server.service.plugin.arthas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ArthasService {
    private final WebClient webClient;

    public ArthasService(@Value("${arthas.api.url:http://localhost:8563/api}") String arthasApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(arthasApiUrl)
                .build();
    }

    public Mono<String> initSession() {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("action", "init_session"))
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> (String) resp.get("sessionId"));
    }

    public Mono<String> joinSession(String sessionId) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("action", "join_session", "sessionId", sessionId))
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> (String) resp.get("consumerId"));
    }

    public Mono<Map> forwardToArthas(Map<String, Object> payload) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
