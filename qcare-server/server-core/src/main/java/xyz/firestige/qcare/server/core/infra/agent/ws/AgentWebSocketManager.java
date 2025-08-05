package xyz.firestige.qcare.server.core.infra.agent.ws;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.model.AgentInfo;

public interface AgentWebSocketManager {
    Mono<Boolean> register(String agentId, WebSocketSession session);

    Mono<Boolean> unregister(String agentId);

    Mono<Boolean> isConnected(AgentInfo info);

    <T> Mono<Boolean> sendMessage(AgentInfo info, T t);

    Mono<Boolean> close(AgentInfo info, String reason);
}
