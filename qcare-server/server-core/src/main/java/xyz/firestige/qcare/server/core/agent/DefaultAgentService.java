package xyz.firestige.qcare.server.core.agent;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

@Service
public class DefaultAgentService implements AgentService {
    @Override
    public Mono<Void> unregisterAgent(String id) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> registerAgent(AgentInfo info) {
        return Mono.empty();
    }
}
