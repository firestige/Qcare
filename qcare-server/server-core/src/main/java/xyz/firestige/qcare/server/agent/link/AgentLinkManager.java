package xyz.firestige.qcare.server.agent.link;

import jakarta.annotation.Nullable;

public interface AgentLinkManager {
    /**
     * 检查是否存在指定的 agentId
     *
     * @param agentId 代理 ID
     * @return 如果存在返回 true，否则返回 false
     */
    boolean hasAgentId(@Nullable String agentId);
}
