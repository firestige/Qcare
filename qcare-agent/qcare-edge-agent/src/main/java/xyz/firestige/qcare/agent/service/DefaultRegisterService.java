package xyz.firestige.qcare.agent.service;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.reactor.http.client.ReactorHttpClient;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.AgentContextHolder;
import xyz.firestige.qcare.agent.AgentInfo;
import xyz.firestige.qcare.agent.event.AgentLinkEstablishedEvent;
import xyz.firestige.qcare.protocol.api.agent.http.AgentRegisterRequest;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class DefaultRegisterService implements RegisterService {
    private static final Logger log = LoggerFactory.getLogger(DefaultRegisterService.class);
    @Inject
    private AgentContextHolder holder;
    @Inject
    @Client
    private ReactorHttpClient httpClient;
    private String currentLeaderHost;

    @EventListener
    public void onAgentEvent(AgentLinkEstablishedEvent event) {
        String agentId = event.getAgentId();
        AgentInfo info = holder.getAgentInfo(agentId);
    }

    @Override
    public Mono<String> requireLeaderHost(String host) {
        String url = UriBuilder.of("http://" + host).path("/api/cluster/leader").build().toString();
        return httpClient.retrieve(HttpRequest.GET(url), String.class)
                .doOnSuccess(leaderHost -> this.currentLeaderHost = leaderHost)
                .doOnError(e -> processError(e, url))
                .log();
    }

    @Override
    public Mono<String> register(AgentInfo info) {
        String url = UriBuilder.of("http://" + this.currentLeaderHost).path("/api/agent/").build().toString();
        AgentRegisterRequest body = new AgentRegisterRequest(info.getId(), info.getName(), info.getType(), info.getVersion());
        return httpClient.retrieve(HttpRequest.POST(url, body), String.class)
                .doOnError(e -> processError(e, url))
                .log();
    }

    private void processError(Throwable throwable, String url) {
        log.error("request url[{}] failed, msg:{}", url, throwable.getMessage());
    }

    @Override
    public boolean unregister(String agentId) {
        return false;
    }
}
