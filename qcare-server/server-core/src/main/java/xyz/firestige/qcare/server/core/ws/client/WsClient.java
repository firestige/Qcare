package xyz.firestige.qcare.server.core.ws.client;

import java.net.URI;

import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.protocol.api.Message;
import xyz.firestige.qcare.server.core.ws.link.WebSocketSessionManager;

/**
 * WebSocket客户端统一入口
 * 封装WebSocketSessionManager的消息发送逻辑
 */
public class WsClient {

    private final String url;
    private final WebSocketSessionManager sessionManager;
    private final WebSocketClient webSocketClient;
    private WebSocketSession currentSession;

    public WsClient(String url, WebSocketSessionManager sessionManager) {
        this.url = url;
        this.sessionManager = sessionManager;
        this.webSocketClient = new ReactorNettyWebSocketClient();
    }

    /**
     * 连接WebSocket
     */
    public Mono<Void> connect() {
        return webSocketClient.execute(URI.create(url), session -> {
            // 注册会话到管理器
            sessionManager.registerSession(url, session);
            this.currentSession = session;

            // 处理接收到的消息
            return session.receive()
                    .map(message -> {
                        try {
                            // 解析消息并委托给会话管理器处理
                            String messageText = message.getPayloadAsText();
                            Message<?> parsedMessage = parseMessage(messageText);
                            sessionManager.handleReceivedMessage(url, parsedMessage);
                            return parsedMessage;
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse message", e);
                        }
                    })
                    .then();
        });
    }

    /**
     * 断开连接
     */
    public Mono<Void> disconnect() {
        if (currentSession != null) {
            return currentSession.close();
        }
        return Mono.empty();
    }

    /**
     * 推送消息（不关心响应）
     */
    public Mono<Void> send(Message<?> message) {
        return sessionManager.send(url, message);
    }

    /**
     * 发送请求并等待响应
     */
    public <RESP> Mono<RESP> sendRequest(Message<?> request, Class<RESP> responseType) {
        return sessionManager.sendRequest(url, request, responseType);
    }

    /**
     * 发送请求并订阅事件流
     */
    public <Event> Flux<Event> sendAndSubscribe(Message<?> request, Class<Event> eventType) {
        return sessionManager.sendAndSubscribe(url, request, eventType);
    }

    /**
     * 获取消息流
     */
    public Flux<Message<?>> getMessageStream() {
        return sessionManager.getMessageStream(url);
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return currentSession != null && currentSession.isOpen();
    }

    /**
     * 获取URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 解析消息
     */
    private Message<?> parseMessage(String messageText) throws Exception {
        // 这里需要ObjectMapper，可以通过构造器注入或静态方法获取
        // 简化实现，实际应该使用注入的ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return mapper.readValue(messageText, Message.class);
    }

    // 便利方法：创建消息
    public static <T> Message<T> createMessage(String route, T payload) {
        Message<T> message = new Message<>();
        message.setRoute(route);
        message.setPayload(payload);
        return message;
    }

    public static <T> Message<T> createRequest(String route, T payload) {
        Message<T> message = createMessage(route, payload);
        message.setType("request");
        return message;
    }

    public static <T> Message<T> createSubscription(String route, T payload) {
        Message<T> message = createMessage(route, payload);
        message.setType("subscribe");
        return message;
    }
}
