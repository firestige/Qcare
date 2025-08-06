package xyz.firestige.qcare.server.service.arthas;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ArthasRemoteService {
    Mono<List<ArthasInstance>> getAllArthasInstances();
    Mono<Void> connectToArthasConsole(String instanceId);
    Mono<Void> disconnectFromArthasConsole(String instanceId);
    Mono<String> executeArthasCommand(String instanceId, String command);
    Mono<Void> submitAsyncJob(String instanceId, String jobName, String jobCommand);
    Mono<JobResult> getJobResult(String instanceId, String jobId);
    Mono<Void> cancelJob(String instanceId, String jobId);
}
