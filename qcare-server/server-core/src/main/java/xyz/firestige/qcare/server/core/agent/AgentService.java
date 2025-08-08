package xyz.firestige.qcare.server.core.agent;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

public interface AgentService {
    /**
     * Retrieves the list of all agent instances.
     *
     * @return a list of agent instances
     */
//    AgentInstanceList getAllAgentInstances();

    /**
     * Connects to the specified agent instance.
     *
     * @param instanceId the ID of the agent instance to connect to
     * @return a Mono indicating the completion of the connection operation
     */
//    Mono<Void> connectToAgentConsole(String instanceId);

    /**
     * Disconnects from the specified agent instance.
     *
     * @param instanceId the ID of the agent instance to disconnect from
     * @return a Mono indicating the completion of the disconnection operation
     */
//    Mono<Void> disconnectFromAgentConsole(String instanceId);

    Mono<Void> unregisterAgent(String id);

    Mono<Void> registerAgent(AgentInfo info);
}
