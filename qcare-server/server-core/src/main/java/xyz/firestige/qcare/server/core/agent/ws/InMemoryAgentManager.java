package xyz.firestige.qcare.server.core.agent.ws;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
public class InMemoryAgentManager implements AgentWebSocketManager{
    @Override
    public Mono<Boolean> register(String agentId, WebSocketSession session) {
        return null;
    }

    @Override
    public Mono<Boolean> validateConnectionToken(String token, String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> unregister(String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> isConnected(String agentId) {
        return null;
    }

    @Override
    public <T> Mono<Boolean> sendMessage(String agentId, T t) {
        return null;
    }

    @Override
    public Mono<Boolean> close(String agentId, String reason) {
        return null;
    }
}
