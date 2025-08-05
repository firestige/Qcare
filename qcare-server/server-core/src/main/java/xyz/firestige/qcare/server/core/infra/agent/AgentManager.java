package xyz.firestige.qcare.server.core.infra.agent;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class AgentManager implements AgentManagementService {

    @Override
    public Mono<Boolean> isAgentOnline(String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAgentOnline'");
    }

    @Override
    public Mono<Boolean> hasAgent(String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAgent'");
    }

    @Override
    public Mono<Boolean> handleKeepAlive(String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleKeepAlive'");
    }

    @Override
    public Mono<Boolean> sendMessageToAgent(String agentId, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessageToAgent'");
    }

    @Override
    public Mono<Boolean> sendEventToAgent(String agentId, String eventType, Object eventData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEventToAgent'");
    }

    @Override
    public Mono<Void> registerAgent(String agentId, String agentName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerAgent'");
    }

    @Override
    public Mono<Void> unregisterAgent(String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unregisterAgent'");
    }

    @Override
    public Mono<Void> updateAgentStatus(String agentId, String status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAgentStatus'");
    }

    @Override
    public Mono<Boolean> validateConnectionToken(String token, String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateConnectionToken'");
    }
}
