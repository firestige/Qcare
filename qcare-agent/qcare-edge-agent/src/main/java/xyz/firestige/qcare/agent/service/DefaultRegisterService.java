package xyz.firestige.qcare.agent.service;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.reactor.http.client.websocket.ReactorWebSocketClient;
import io.micronaut.websocket.WebSocketClient;
import io.micronaut.websocket.annotation.ClientWebSocket;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.ws.AgentClientWebSocket;

@Singleton
public class DefaultRegisterService implements RegisterService {

    @Inject
    @Client("http://localhost:8080")
    private ReactorWebSocketClient client;

    @Override
    public boolean register(String agentId, String agentVersion, String agentType) {
        Flux<AgentClientWebSocket> clientFlux = client.connect(AgentClientWebSocket.class, "/ws?agent_id=" + agentId);
        clientFlux.next().subscribe(c -> c.send("123"));
        return true;
    }

    @Override
    public boolean unregister(String agentId) {
        return false;
    }
}
