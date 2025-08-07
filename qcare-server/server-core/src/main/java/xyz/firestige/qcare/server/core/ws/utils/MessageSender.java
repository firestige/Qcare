package xyz.firestige.qcare.server.core.ws.utils;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.Message;

/**
 * WebSocket消息发送工具类
 */
public class MessageSender {
    
    private final ObjectMapper objectMapper;
    
    public MessageSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * 发送消息
     */
    public Mono<Void> sendMessage(WebSocketSession session, Message<?> message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            return session.send(Mono.just(session.textMessage(json)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
    
    /**
     * 发送错误消息
     */
    public Mono<Void> sendError(WebSocketSession session, String error) {
        Message<String> errorMessage = new Message<>();
        errorMessage.setType("error");
        errorMessage.setPayload(error);
        return sendMessage(session, errorMessage);
    }
    
    /**
     * 发送响应消息
     */
    public <T> Mono<Void> sendResponse(WebSocketSession session, String requestId, String route, T payload) {
        Message<T> response = new Message<>(requestId, "response", route, payload);
        return sendMessage(session, response);
    }
}
