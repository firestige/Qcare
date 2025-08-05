package xyz.firestige.qcare.server.core.infra.agent.model;

public record AgentInfo(
        String agentId,
        String agentName,
        String agentVersion,
        String agentType,
        String agentDescription,
        AgentState agentState,
        String agentIpAddress
) {
}
