package xyz.firestige.qcare.server.core.infra.agent.ws;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.model.AgentInfo;

public interface AgentWebSocketManager {
    /**
     * 注册ws连接
     *
     * @param agentId agent id
     * @param session ws会话
     * @return true-成功，false-失败
     */
    Mono<Boolean> register(String agentId, WebSocketSession session);

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @param agentId agent id
     * @return true-成功，false-失败
     */
    Mono<Boolean> validateConnectionToken(String token, String agentId);

    /**
     * 注销ws连接
     *
     * @param agentId agent id
     * @return true-成功，false-失败
     */
    Mono<Boolean> unregister(String agentId);

    /**
     * 判断是否已连接
     *
     * @param agentId agent id
     * @return true-已连接，false-未连接
     */
    Mono<Boolean> isConnected(String agentId);

    /**
     * 发送消息
     *
     * @param agentId agent id
     * @param t 消息
     * @return true-发送成功，false-发送失败
     * @param <T> 消息类型
     */
    <T> Mono<Boolean> sendMessage(String agentId, T t);

    /**
     * 关闭连接
     *
     * @param agentId agent id
     * @param reason 关闭原因
     * @return true-关闭成功，false-关闭失败
     */
    Mono<Boolean> close(String agentId, String reason);
}
