package xyz.firestige.qcare.agent.service;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.AgentInfo;

public interface RegisterService {

    Mono<String> requireLeaderHost(String host);

    /**
     * Register the agent to the server.
     *
     * @param info the metadata of the agent
     * @return wsConnection addr.
     */
    Mono<String> register(AgentInfo info);

    /**
     * Unregister the agent from the server.
     *
     * @param agentId The unique identifier for the agent.
     * @return true if unregistration is successful, false otherwise.
     */
    boolean unregister(String agentId);
}
