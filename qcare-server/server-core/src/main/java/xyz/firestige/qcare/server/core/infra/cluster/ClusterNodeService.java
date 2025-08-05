package xyz.firestige.qcare.server.core.infra.cluster;

import reactor.core.publisher.Mono;

public interface ClusterNodeService {
    Mono<Boolean> validateConnectionToken(String token, String agentId);
}
