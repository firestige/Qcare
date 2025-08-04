package xyz.firestige.qcare.server.core.infra.agent;

import java.util.List;

public interface AgentRegistrationService {
    /**
     * 注册 Agent
     *
     * @param agentId Agent ID
     * @param agentInfo Agent 信息
     * @return 是否注册成功
     */
    boolean registerAgent(String agentId, String agentInfo);

    /**
     * 注销 Agent
     *
     * @param agentId Agent ID
     * @return 是否注销成功
     */
    boolean unregisterAgent(String agentId);

    /**
     * 获取所有已注册的 Agent 列表
     *
     * @return 已注册的 Agent 列表
     */
    List<String> getRegisteredAgents();
}
