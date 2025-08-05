package xyz.firestige.qcare.server.core.infra.agent;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.model.AgentInfo;
import xyz.firestige.qcare.server.core.infra.agent.model.ConnectInfo;

import java.util.List;

public interface AgentRegistrationService {
    /**
     * 注册 Agent
     *
     * @param agentInfo Agent 信息
     * @return 是否注册成功
     * @see AgentInfo
     */
    Mono<ConnectInfo> registerAgent(AgentInfo agentInfo);

    /**
     * 注销 Agent
     *
     * @param agentId Agent ID
     * @return 是否注销成功
     */
    Mono<AgentInfo> unregisterAgent(String agentId);

    /**
     * 获取所有已注册的 Agent 列表
     *
     * @return 已注册的 Agent 列表
     */
    List<String> getRegisteredAgents();
}
