package xyz.firestige.qcare.server.api.vo;

import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

public record AgentRegisterRequest(
    AgentInfo info
) {
}
