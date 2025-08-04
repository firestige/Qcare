package xyz.firestige.qcare.server.core.infra.agent;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
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

    @PostMapping("/register")
    public Mono<ServerResponse> registerAgent(String agentId) {
        if (clusterManagementService.isLeader()) {
            return agentRegistrationService.registerAgent(agentId)
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .onErrorResume(e -> ServerResponse.status(500).bodyValue("Registration failed: " + e.getMessage()));
        } else {
            URI uri = clusterManagementService.getLeaderUri();
            return ServerResponse.permanentRedirect(uri).build();
        }
    }
}
