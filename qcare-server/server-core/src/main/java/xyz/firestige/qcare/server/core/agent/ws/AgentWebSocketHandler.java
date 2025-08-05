package xyz.firestige.qcare.server.core.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class AgentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);
//    private final ObjectMapper objectMapper;

    public AgentWebSocketHandler(AgentWebSocketManager webSocketManager,
                                 ObjectMapper objectMapper,
                                 DispatchHandler dispatcher) {
//        this.objectMapper = objectMapper;
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        String agentId = extractAgentId(session);

        if (agentId == null) {
            return session.close();
        }

        return session.receive().flatMap(this::handleMessage).then();
    }

    private String extractAgentId(WebSocketSession session) {
        String queryParam = session.getHandshakeInfo().getUri().getQuery();
        if (queryParam != null && queryParam.startsWith("agentId=")) {
            return queryParam.substring("agentId=".length());
        } else {
            log.warn("No agentId found in WebSocket session: {}", session.getId());
            return null;
        }
    }

    private Mono<Void> handleMessage(@NonNull WebSocketMessage msg) {
        try {
            String payload = msg.getPayloadAsText();
            // Process the message payload here
            log.info("Received message: {}", payload);
            // You can add your logic to handle the message
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            return Mono.error(e);
        }
        return Mono.empty();
    }
}
