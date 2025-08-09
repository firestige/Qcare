package xyz.firestige.qcare.server.api;

import java.net.URI;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.protocol.api.agent.http.AgentRegisterRequest;
import xyz.firestige.qcare.server.core.agent.AgentService;
import xyz.firestige.qcare.server.core.agent.model.AgentInfo;
import xyz.firestige.qcare.server.core.cluster.ClusterManagementService;

@RestController
@RequestMapping("/api/agent")
public class AgentController {
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
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
    public Mono<ResponseEntity<String>> registerAgent(AgentRegisterRequest request) {
        if (clusterManagementService.isLeader()) {
            log.info("Register agent request has been sent to leader");
            AgentInfo info = new AgentInfo(request.id(), request.name());
            return agentService.registerAgent(info)
                    .then(Mono.defer(() -> Mono.just(ResponseEntity.ok("localhost:8080"))));
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:8080/api/agent/"));
            return Mono.just(ResponseEntity.status(HttpStatus.FOUND).headers(headers).build());
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
