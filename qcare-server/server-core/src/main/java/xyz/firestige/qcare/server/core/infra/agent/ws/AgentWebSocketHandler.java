package xyz.firestige.qcare.server.core.infra.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.infra.agent.AgentManagementService;

@Component
public class AgentWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);
    private final AgentWebSocketManager webSocketManager;
    private final ObjectMapper objectMapper;
    private final DispatchHandler dispatcher;

    public AgentWebSocketHandler(AgentWebSocketManager webSocketManager,
                                 ObjectMapper objectMapper,
                                 DispatchHandler dispatcher) {
        this.webSocketManager = webSocketManager;
        this.objectMapper = objectMapper;
        this.dispatcher = dispatcher;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String agentId = extractAgentId(session);
        String token = extractToken(session);

        if (agentId == null) {
            return session.close();
        }


        return webSocketManager.validateConnectionToken(token, agentId)
            .flatMap(valid -> {
                if (!valid) {
                    return session.close();
                }
                return register(agentId, session);
            })
            .onErrorResume(e -> {
                // 处理连接错误
                return session.close();
            }).doFinally(signal ->{
                // 标记Agent下线
            });
    }

    private String extractAgentId(WebSocketSession session) {
        return null;
    }

    private String extractToken(WebSocketSession session) {
        return null;
    }

    private Mono<Void> register(String agentId, WebSocketSession session) {
        return webSocketManager.register(agentId, session)
                .flatMap(isSuccess -> {
                    if (!isSuccess) {
                        return session.close();
                    }
                    return handleAgentMessage(agentId, session);
                });
    }

    private Mono<Void> handleAgentMessage(String agentId, WebSocketSession session) {
        return session.receive().doOnNext(msg -> {
            try {
                String payload = msg.getPayloadAsText();
                GenericMessage genericMessage = objectMapper.readValue(payload, GenericMessage.class);
                dispatcher.dispatch(genericMessage, agentId)
                    .doOnError(e -> log.error("处理消息失败: {}", e.getMessage()))
                    .doFinally(unused -> log.trace("finished dispatching message: {}", genericMessage))
                    .subscribe();
            } catch (Exception e) {
                // 处理消息解析错误
            }
        }).then();
    }
}
