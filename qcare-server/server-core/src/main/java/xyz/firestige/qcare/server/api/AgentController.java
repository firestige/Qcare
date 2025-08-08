package xyz.firestige.qcare.server.api;

import java.net.URI;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.api.vo.AgentRegisterRequest;
import xyz.firestige.qcare.server.core.agent.AgentService;
import xyz.firestige.qcare.server.core.cluster.ClusterManagementService;

@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private final AgentService agentService;
    private final ClusterManagementService clusterManagementService;

    public AgentController(AgentService agentService, ClusterManagementService clusterManagementService) {
        this.agentService = agentService;
        this.clusterManagementService = clusterManagementService;
    }

    @GetMapping("/")
    public Mono<ServerResponse> getAllAgentInfo() {
        return Mono.empty();
    }

    @GetMapping("/{id}")
    public Mono<ServerResponse> getAgentInfo(@PathVariable String id) {
        return Mono.empty();
    }

    @PostMapping("/")
    public Mono<ServerResponse> registerAgent(@RequestBody AgentRegisterRequest request) {
        if (clusterManagementService.isLeader()) {
            return agentService.registerAgent(request.info())
                    .flatMap(response -> ServerResponse.ok().bodyValue(response))
                    .onErrorResume(e -> ServerResponse.status(500).bodyValue("Registration failed: " + e.getMessage()));
        } else {
            String leaderHost = clusterManagementService.getLeaderHost();
            URI uri = URI.create("http://" + leaderHost + "/api/agent/");
            return ServerResponse.permanentRedirect(uri).build();
        }
    }

    @DeleteMapping("/{id}")
    public Mono<ServerResponse> unregisterAgent(@PathVariable String id) {
        if (clusterManagementService.isLeader()) {
            return agentService.unregisterAgent(id)
                    .flatMap(info -> ServerResponse.ok().bodyValue(info))
                    .switchIfEmpty(Mono.defer(() -> ServerResponse.notFound().build()))
                    .onErrorResume(e -> ServerResponse.status(500).bodyValue("Registration failed: " + e.getMessage()));
        } else {
            String host = clusterManagementService.getLeaderHost();
            URI uri = URI.create("http://" + host + "/api/agent/" + id);
            return ServerResponse.permanentRedirect(uri).build();
        }
    }
}
