package xyz.firestige.qcare.agent.event;

import java.time.Instant;

public class AgentEvent {
    private final String agentId;
    private final Instant timestamp;

    public AgentEvent(String agentId) {
        this.agentId = agentId;
        this.timestamp = Instant.now();
    }

    public String getAgentId() {
        return agentId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
