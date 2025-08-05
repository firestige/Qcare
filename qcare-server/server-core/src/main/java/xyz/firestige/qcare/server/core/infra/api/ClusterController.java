package xyz.firestige.qcare.server.core.infra.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.AgentRegistrationService;
import xyz.firestige.qcare.server.core.infra.cluster.ClusterManagementService;

@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    private AgentRegistrationService agentRegistrationService;
    private ClusterManagementService clusterManagementService;

    @PostMapping("/agent/register")
    public Mono<ServerResponse> registerAgent(ServerRequest serverRequest) {
        return
    }

    @GetMapping("/agent")
    public Mono<ServerResponse> getAllAgents() {
        return
    }
}
