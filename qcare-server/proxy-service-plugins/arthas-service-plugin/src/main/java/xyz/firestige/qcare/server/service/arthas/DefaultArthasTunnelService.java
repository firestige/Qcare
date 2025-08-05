package xyz.firestige.qcare.server.service.arthas;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultArthasTunnelService implements ArthasRemoteService {

    private final Map<String, String> instanceMap = new ConcurrentHashMap<>();

    @EventListener(classes = AgentRegisteredEvent.class)
    public void onAgentRegistered(AgentRegisteredEvent event) {

        addAgentConnectionInfo()
    }


    @Override
    public Mono<Void> initArthasInstances() {
        return null;
    }

    @Override
    public Mono<List<ArthasInstance>> getAllArthasInstances() {
        return null;
    }

    @Override
    public Mono<Void> connectToArthasConsole(String instanceId) {
        WebClient client = getClient(instanceId);
        String sessionId = getSessionId(instanceId);
        if (sessionId == null) {
            response = initSession(instanceId, client);
            updateSessionId(instanceId, response);
            updateConsumerId(instanceId, response);
        } else {
            response = joinSession(instanceId, client, sessionId);
            updateSessionId(instanceId, response);
            updateConsumerId(instanceId, response);
        }
        return Mono.empty();
    }

    private WebClient getClient(String instanceId) {
        return WebClient.create(instanceMap.get(instanceId));
    }

    @Override
    public Mono<Void> disconnectFromArthasConsole(String instanceId) {
        return null;
    }

    @Override
    public Mono<String> executeArthasCommand(String instanceId, String command) {
        return null;
    }

    @Override
    public Mono<Void> submiteAsyncJob(String instanceId, String jobName, String jobCommand) {
        return null;
    }

    @Override
    public Mono<JobResult> getJobResult(String instanceId, String jobId) {
        return null;
    }

    @Override
    public Mono<Void> cancelJob(String instanceId, String jobId) {
        return null;
    }
}
