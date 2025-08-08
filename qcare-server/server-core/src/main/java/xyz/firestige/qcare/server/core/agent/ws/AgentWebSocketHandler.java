package xyz.firestige.qcare.server.core.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.firestige.qcare.server.core.ws.CloseStatusWrapper;
import xyz.firestige.qcare.server.core.ws.link.WebSocketLifeCircleAware;
import xyz.firestige.qcare.server.core.ws.server.DefaultWsExchange;
import xyz.firestige.qcare.protocol.api.Message;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.WsHandler;
import xyz.firestige.qcare.server.core.ws.server.annotation.WebSocket;

import java.net.URI;
import java.util.Objects;

@WebSocket(path = "/ws")
public class AgentWebSocketHandler implements WebSocketLifeCircleAware {

    private static final Logger log = LoggerFactory.getLogger(AgentWebSocketHandler.class);
    private final WsHandler dispatcher;
    private final ObjectMapper mapper;
    private final AgentWebSocketManager manager;

    public AgentWebSocketHandler(@Qualifier("dispatcher") WsHandler dispatcher, AgentWebSocketManager manager, ObjectMapper objectMapper) {
        this.dispatcher = dispatcher;
        this.manager = manager;
        this.mapper = objectMapper;
    }


    @Override
    public Mono<Void> onOpen(WebSocketSession session) {
        String agentId = extraAgentId(session);
        if (Objects.isNull(agentId)) {
            log.warn("agentId should not be null");
            return session.close(CloseStatusWrapper.BAD_REQUEST);
        }
        return manager.join(agentId, session);
    }

    private String extraAgentId(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        String query = uri.getQuery();
        for (String key : query.split("&")) {
            if (key.startsWith("agent_id=")) {
                return key.split("=")[1];
            }
        }
        return null;
    }

    @Override
    public Mono<Void> onMessage(WebSocketSession session, WebSocketMessage message) {
        Message<?> payload = extraPayloadFromWebSocketMessage(message);
        WsExchange exchange = new DefaultWsExchange(session, payload);
        return dispatcher.handle(exchange);
    }

    private Message<?> extraPayloadFromWebSocketMessage(WebSocketMessage message) {
        try {
            String payloadText = message.getPayloadAsText();
            if (log.isDebugEnabled()) {
                log.debug("Received WebSocket message: {}", payloadText);
            }
            
            // 首先解析为 JsonNode，保持原始结构
            JsonNode jsonNode = mapper.readTree(payloadText);
            
            // 创建 Message 实例，payload 保持为 JsonNode
            Message<JsonNode> rawMessage = new Message<>();
            rawMessage.setId(getTextValue(jsonNode, "id"));
            rawMessage.setType(getTextValue(jsonNode, "type"));
            rawMessage.setRoute(getTextValue(jsonNode, "route"));
            rawMessage.setAction(getTextValue(jsonNode, "action"));
            
            // payload 保持为 JsonNode，让后续的参数解析器处理具体类型转换
            JsonNode payloadNode = jsonNode.get("payload");
            rawMessage.setPayload(payloadNode);
            
            return rawMessage;
        } catch (Exception e) {
            log.error("Failed to parse WebSocket message", e);
            throw new IllegalArgumentException("Invalid WebSocket message format", e);
        }
    }

    private String getTextValue(JsonNode jsonNode, String fieldName) {
        JsonNode fieldNode = jsonNode.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    @Override
    public void onError(Throwable error, WebSocketSession session) {
        log.error(error.getMessage(), error);
        session.close(CloseStatusWrapper.INTERNAL_SERVER_ERROR)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Override
    public void onClose(WebSocketSession session) {
        String agentId = extraAgentId(session);
        manager.leave(agentId);
    }
}
