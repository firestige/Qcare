package xyz.firestige.qcare.server.core.agent;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.model.AgentState;

public interface AgentManagementService {
    /**
     * 查询Agent在线状态
     *
     * @param agentId agent id
     * @return true-在线，false-离线
     */
    Mono<Boolean> isAgentOnline(String agentId);

    /**
     * 处理保活
     *
     * @param agentId agent id
     * @return true-成功，false-不成功
     */
    Mono<Boolean> handleKeepAlive(String agentId);

    /**
     * 向Agent发送字符串消息
     *
     * @param agentId agent id
     * @param message 消息
     * @return true-成功，false-不成功
     */
    Mono<Boolean> sendMessageToAgent(String agentId, String message);

    /**
     * 向Agent发送事件
     *
     * @param agentId agent id
     * @param eventType 事件类型
     * @param eventData 事件
     * @return true-成功，false-失败
     */
    Mono<Boolean> sendEventToAgent(String agentId, String eventType, Object eventData);

    /**
     * 更新Agent状态
     *
     * @param agentId agent id
     * @param status agent state
     * @return void
     */
    Mono<Void> updateAgentStatus(String agentId, AgentState status);
}
