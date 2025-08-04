package xyz.firestige.qcare.server.core.infra.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.cluster.ClusterManagementService;

import java.util.List;

@Component
public class AgentWebSocketHandler implements WebSocketHandler {

    private final AgentWebSocketManager webSocketManager;
    private final ClusterManagementService clusterManagementService;
    private final ClusterNodeService clusterNodeService;
    private final ObjectMapper objectMapper;

    public AgentWebSocketHandler(AgentWebSocketManager webSocketManager,
                                 ClusterManagementService clusterManagementService,
                                 ClusterNodeService clusterNodeService,
                                 ObjectMapper objectMapper) {
        this.webSocketManager = webSocketManager;
        this.clusterManagementService = clusterManagementService;
        this.clusterNodeService = clusterNodeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> getSubProtocols() {
        return WebSocketHandler.super.getSubProtocols();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
