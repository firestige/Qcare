package xyz.firestige.qcare.server.core.infra.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.AgentManagementService;
import xyz.firestige.qcare.server.core.infra.agent.AgentRegistrationService;
import xyz.firestige.qcare.server.core.infra.agent.model.vo.AgentRegisterRequest;
import xyz.firestige.qcare.server.core.infra.cluster.ClusterManagementService;

import java.net.URI;

@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private final AgentRegistrationService agentRegistrationService;
    private final AgentManagementService agentManagementService;
    private final ClusterManagementService clusterManagementService;

    public AgentController(AgentRegistrationService agentRegistrationService, AgentManagementService agentManagementService, ClusterManagementService clusterManagementService) {
        this.agentRegistrationService = agentRegistrationService;
        this.agentManagementService = agentManagementService;
        this.clusterManagementService = clusterManagementService;
    }

    @PostMapping("/")
    public Mono<ServerResponse> registerAgent(@RequestBody AgentRegisterRequest request) {
        if (clusterManagementService.isLeader()) {
            return agentRegistrationService.registerAgent(request.info())
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .onErrorResume(e -> ServerResponse.status(500).bodyValue("Registration failed: " + e.getMessage()));
        } else {
            URI uri = clusterManagementService.getLeaderUri();
            return ServerResponse.permanentRedirect(uri).build();
        }
    }

    @DeleteMapping("/{id}")
    public Mono<ServerResponse> unregisterAgent(@PathVariable String id) {
        if (clusterManagementService.isLeader()) {
            return agentRegistrationService.unregisterAgent(id)
                    .flatMap(info -> ServerResponse.ok().bodyValue(info))
                    .switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()))
                    .onErrorResume(e -> ServerResponse.status(500).bodyValue("Registration failed: " + e.getMessage()));
        } else {
            URI uri = clusterManagementService.getLeaderUri();
            return ServerResponse.permanentRedirect(uri).build();
        }
    }
}
