package xyz.firestige.qcare.server.core.agent.ws;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DispatchHandler {
    public Mono<Void> dispatch(GenericMessage message, String agentId) {
        return Mono.empty();
    }
}
