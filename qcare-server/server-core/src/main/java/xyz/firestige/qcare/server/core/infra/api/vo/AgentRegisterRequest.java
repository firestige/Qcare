package xyz.firestige.qcare.server.core.infra.api.vo;

import xyz.firestige.qcare.server.core.infra.agent.model.AgentInfo;

public record AgentRegisterRequest(
    AgentInfo info
) {
}
