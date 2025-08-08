package xyz.firestige.qcare.agent.service;

public interface RegisterService {
    /**
     * Register the agent to the server.
     *
     * @param agentId The unique identifier for the agent.
     * @param agentVersion The version of the agent.
     * @param agentType The type of the agent (e.g., "edge").
     * @return true if registration is successful, false otherwise.
     */
    boolean register(String agentId, String agentVersion, String agentType);
    /**
     * Unregister the agent from the server.
     *
     * @param agentId The unique identifier for the agent.
     * @return true if unregistration is successful, false otherwise.
     */
    boolean unregister(String agentId);
}
