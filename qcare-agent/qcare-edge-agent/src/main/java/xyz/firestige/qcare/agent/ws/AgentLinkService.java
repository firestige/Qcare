package xyz.firestige.qcare.agent.ws;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.reactor.http.client.websocket.ReactorWebSocketClient;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.util.retry.Retry;
import xyz.firestige.qcare.agent.AgentInfo;
import xyz.firestige.qcare.agent.event.AgentLinkEstablishedEvent;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Singleton
public class AgentLinkService {
    @Property(name = "qcare.server.ws.base_url", defaultValue = "ws://localhost:8080/ws")
    private String baseUrl;

    @Inject
    private ApplicationEventPublisher<AgentLinkEstablishedEvent> linkPublisher;

    @Inject
    @Client
    private ReactorWebSocketClient client;

    private final AgentLink agentLink = new AgentLink();

    public void initWsLink(AgentInfo info) {
        initWsLink("ws://" + info.getWsHost(), info.getId());
    }

    public void initWsLink(String baseUrl, String agentId) {
        URI uri = UriBuilder.of(baseUrl)
                .path("/ws")
                .queryParam("agent_id", agentId)
                .build();
        client.connect(AgentClientWebSocket.class, uri)
                .next()
                .subscribe(agentLink::setLink);
    }

    public AgentLink getAgentLink() {
        return agentLink;
    }
}
