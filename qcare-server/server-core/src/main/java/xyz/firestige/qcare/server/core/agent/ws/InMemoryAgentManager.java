package xyz.firestige.qcare.server.core.agent.ws;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAgentManager implements AgentWebSocketManager{

    private Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Boolean> join(String agentId, WebSocketSession session) {

        return null;
    }

    @Override
    public Mono<Boolean> validateConnectionToken(String token, String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> leave(String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> isConnected(String agentId) {
        return null;
    }
}
