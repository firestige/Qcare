package xyz.firestige.qcare.server.core.agent.ws;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.model.AgentInfo;

public interface AgentWebSocketManager {
    /**
     * 为agent加入ws会话
     * 每一个agentId只能绑定一个ws会话，新的会话将踢掉旧的会话
     *
     * @param agentId agent id
     * @param session ws会话
     * @return true-成功，false-失败
     */
    Mono<Boolean> join(String agentId, WebSocketSession session);

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @param agentId agent id
     * @return true-成功，false-失败
     */
    Mono<Boolean> validateConnectionToken(String token, String agentId);

    /**
     * 为agent移除ws会话
     * 移除ws会话的同时会
     *
     * @param agentId agent id
     * @return true-成功，false-失败
     */
    Mono<Boolean> leave(String agentId);

    /**
     * 判断是否已连接
     *
     * @param agentId agent id
     * @return true-已连接，false-未连接
     */
    Mono<Boolean> isConnected(String agentId);
}
