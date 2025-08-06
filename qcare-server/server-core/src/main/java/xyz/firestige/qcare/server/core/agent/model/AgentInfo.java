package xyz.firestige.qcare.server.core.agent.model;

import java.util.Map;

public record AgentInfo(
        String agentId,
        String agentName,
        AgentState agentState,
        String agentIpAddress,
        Map<String, String> agentMetadata
) {
}
