package xyz.firestige.qcare.server.core.agent.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.proto.ws.Message;

@Component
public class AgentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);
    private final ObjectMapper objectMapper;
    private final AgentWebSocketManager manager;

    public AgentWebSocketHandler(AgentWebSocketManager webSocketManager,
                                 ObjectMapper objectMapper,
                                 DispatchHandler dispatcher) {
        this.objectMapper = objectMapper;
        this.manager = webSocketManager;
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

    private Mono<Void> handleMessage(String agentId, @NonNull WebSocketMessage msg) {
        try {
            WsMessage wsMsg = parseMessage(msg);
            if (wsMsg == null) {
                return Mono.empty();
            }
            switch (wsMsg.msgType()) {
                case "REQUEST":
                    return manager.handleRequest(agentId, wsMsg);
                case "RESPONSE":
                    return manager.handleResponse(agentId, wsMsg);
                case "KEEPALIVE":
                    return manager.handleKeepalive(agentId);
                case "UNREGISTER":
                    return manager.handleUnregister(agentId);
                case "REGISTER":
                    return manager.handleRegister(agentId);
                default:
                    return Mono.empty();
            }
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            return Mono.error(e);
        }
        return Mono.empty();
    }

    private WsMessage parseMessage(WebSocketMessage msg) throws JsonProcessingException {
        if (msg.getType() == WebSocketMessage.Type.TEXT) {
            String data = msg.getPayloadAsText();
            return objectMapper.readValue(data, WsMessage.class);

        }
        return null;
    }
}
