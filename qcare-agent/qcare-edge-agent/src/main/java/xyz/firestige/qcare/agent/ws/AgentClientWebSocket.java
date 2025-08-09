package xyz.firestige.qcare.agent.ws;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import reactor.core.publisher.Mono;

@ClientWebSocket("/ws?agent_id={agentId}")
abstract class AgentClientWebSocket implements AutoCloseable{
    private String agentId;
    private WebSocketSession session;

    @OnOpen
    public void onOpen(String agentId, WebSocketSession session) {
        this.agentId = agentId;
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        // Handle incoming messages
    }

    public abstract void send(String message);
    public abstract Mono<String> sendAsync(String message);

    public boolean isConnected() {
        return session != null && session.isOpen();
    }
}
