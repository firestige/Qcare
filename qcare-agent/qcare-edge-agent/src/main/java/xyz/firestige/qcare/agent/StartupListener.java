package xyz.firestige.qcare.agent;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import xyz.firestige.qcare.agent.config.AgentConfiguration;
import xyz.firestige.qcare.agent.service.RegisterService;
import xyz.firestige.qcare.agent.ws.AgentLinkService;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class StartupListener {
    @Inject
    private AgentConfiguration configuration;

    @Inject
    private AgentContextHolder holder;

    @Inject
    private AgentLinkService linkService;

    @Inject
    private RegisterService registerService;

    @EventListener
    public void onStartup(StartupEvent event) {
        String agentId = Optional.ofNullable(configuration.getId()).orElseGet(() -> UUID.randomUUID().toString());
        String name = configuration.getName();
        String host = configuration.getRemoteAddress();
        AgentInfo earlyInfo = new AgentInfo(agentId, name, "edge", "1.0.0", host);
        registerService.requireLeaderHost(host)
                .flatMap(url -> registerService.register(earlyInfo))
                .map(earlyInfo::copyWithWsHost)
                .doOnNext(info -> holder.replaceIfPresent(info))
                .subscribe(linkService::initWsLink);
    }


}
