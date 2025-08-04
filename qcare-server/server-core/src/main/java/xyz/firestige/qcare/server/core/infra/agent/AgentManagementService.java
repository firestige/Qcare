package xyz.firestige.qcare.server.core.infra.agent;

import reactor.core.publisher.Mono;

public interface AgentManagementService {
    Mono<Boolean> isAgentOnline(String agentId);
    Mono<Boolean> hasAgent(String agentId);
    Mono<Void> handleKeepAlive(String agentId);
    Mono<Boolean> sendMessageToAgent(String agentId, String message);
    Mono<Boolean> sendEventToAgent(String agentId, String eventType, Object eventData);
    Mono<Void> registerAgent(String agentId, String agentName);
    Mono<Void> unregisterAgent(String agentId);
    Mono<Void> updateAgentStatus(String agentId, String status);
}
