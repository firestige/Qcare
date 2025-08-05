package xyz.firestige.qcare.server.core.agent;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.model.AgentInfo;
import xyz.firestige.qcare.server.core.agent.model.AgentState;
import xyz.firestige.qcare.server.core.agent.model.ConnectInfo;

import java.util.List;

@Service
public class AgentManager implements AgentManagementService, AgentRegistrationService {

    @Override
    public Mono<Boolean> isAgentOnline(String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> handleKeepAlive(String agentId) {
        return null;
    }

    @Override
    public Mono<Boolean> sendMessageToAgent(String agentId, String message) {
        return null;
    }

    @Override
    public Mono<Boolean> sendEventToAgent(String agentId, String eventType, Object eventData) {
        return null;
    }

    @Override
    public Mono<Void> updateAgentStatus(String agentId, AgentState status) {
        return null;
    }

    @Override
    public Mono<ConnectInfo> registerAgent(AgentInfo agentInfo) {
        return null;
    }

    @Override
    public Mono<AgentInfo> unregisterAgent(String agentId) {
        return null;
    }

    @Override
    public List<AgentInfo> getRegisteredAgents() {
        return List.of();
    }

    @Override
    public Mono<Boolean> hasAgent(String agentId) {
        return null;
    }
}
