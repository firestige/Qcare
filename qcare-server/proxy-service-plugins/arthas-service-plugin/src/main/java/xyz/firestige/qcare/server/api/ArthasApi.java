package xyz.firestige.qcare.server.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.service.arthas.ArthasRemoteService;

@RestController
@RequestMapping("/api/arthas")
public class ArthasApi {

    private ArthasRemoteService service;

    public ArthasApi(ArthasRemoteService service) {
        this.service = service;
    }

    @GetMapping("/summery")
    public Mono<ServerResponse> getRuntimeSummery() {
        return service.getRuntimeSummery()
               .flatMap(summery -> ServerResponse.ok().bodyValue(summery))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error fetching runtime summary: " + e.getMessage()));
    }

    @GetMapping("/{id}/connect")
    public Mono<ServerResponse> connectToArthas(@PathVariable String id) {
        return service.connectToArthasConsole(id)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error connecting to Arthas: " + e.getMessage()));
    }

    @GetMapping("/{id}/disconnect")
    public Mono<ServerResponse> disconnectFromArthas(@PathVariable String id) {
        return service.disconnectFromArthasConsole(id)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error disconnecting from Arthas: " + e.getMessage()));
    }

    @PostMapping("/{id}/commands")
    public Mono<ServerResponse> executeArthasCommand(@PathVariable String id, @RequestBody String command) {
        return service.executeArthasCommand(id, command)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error executing Arthas command: " + e.getMessage()));
    }

    @PostMapping("/{id}/job")
    public Mono<ServerResponse> submitArthasJob(@PathVariable String id, @RequestBody String jobDetails) {
        return service.submitArthasJob(id, jobDetails)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error submitting Arthas job: " + e.getMessage()));
    }

    @GetMapping("/{id}/job_results/{jobId}")
    public Mono<ServerResponse> getArthasJobResults(@PathVariable String id, @PathVariable String jobId) {
        return service.getArthasJobResults(id, jobId)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error fetching Arthas job results: " + e.getMessage()));
    }

    @DeleteMapping("/{id}/job/{jobId}")
    public Mono<ServerResponse> cancelArthasJob(@PathVariable String id, @PathVariable String jobId) {
        return service.cancelArthasJob(id, jobId)
               .flatMap(response -> ServerResponse.ok().bodyValue(response))
               .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error canceling Arthas job: " + e.getMessage()));
    }
}
