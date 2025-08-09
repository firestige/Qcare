package xyz.firestige.qcare.server.core.agent.model;

import java.util.Map;

public class AgentInfo {
    private final String agentId;
    private final String agentName;
    private AgentState agentState;
    private String agentIpAddress;
    private Map<String, String> agentMetadata;
    public AgentInfo(String agentId, String agentName) {
        this.agentId = agentId;
        this.agentName = agentName;
    }
}
