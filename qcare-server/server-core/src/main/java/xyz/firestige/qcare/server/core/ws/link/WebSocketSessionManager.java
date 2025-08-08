package xyz.firestige.qcare.server.core.ws.link;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import xyz.firestige.qcare.protocol.api.Message;

/**
 * WebSocket会话管理器
 * 负责管理WebSocket会话，提供消息发送功能
 */
@Component
public class WebSocketSessionManager {

    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<Message<?>>> responseStreams = new ConcurrentHashMap<>();
    private final AtomicLong messageIdGenerator = new AtomicLong(0);

    // 存储等待响应的请求
    private final Map<String, Sinks.One<Message<?>>> pendingRequests = new ConcurrentHashMap<>();
    
    // 存储事件流订阅
    private final Map<String, Sinks.Many<Message<?>>> eventStreams = new ConcurrentHashMap<>();

    public WebSocketSessionManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 注册会话
     */
    public void registerSession(String url, WebSocketSession session) {
        sessions.put(url, session);
        
        // 为每个会话创建响应流
        Sinks.Many<Message<?>> responseStream = Sinks.many().multicast().onBackpressureBuffer();
        responseStreams.put(url, responseStream);
        
        // 监听会话关闭
        session.closeStatus().subscribe(closeStatus -> {
            unregisterSession(url);
        });
    }

    /**
     * 注销会话
     */
    public void unregisterSession(String url) {
        sessions.remove(url);
        
        // 清理响应流
        Sinks.Many<Message<?>> responseStream = responseStreams.remove(url);
        if (responseStream != null) {
            responseStream.tryEmitComplete();
        }
        
        // 清理事件流
        eventStreams.entrySet().removeIf(entry -> entry.getKey().startsWith(url + ":"));
        
        // 清理待响应的请求
        pendingRequests.entrySet().removeIf(entry -> entry.getKey().startsWith(url + ":"));
    }

    /**
     * 获取会话
     */
    public WebSocketSession getSession(String url) {
        return sessions.get(url);
    }

    /**
     * 处理接收到的消息
     */
    public void handleReceivedMessage(String url, Message<?> message) {
        String messageId = message.getId();
        String type = message.getType();
        
        if ("response".equals(type) && messageId != null) {
            // 处理响应消息
            String requestKey = url + ":" + messageId;
            Sinks.One<Message<?>> pendingSink = pendingRequests.remove(requestKey);
            if (pendingSink != null) {
                pendingSink.tryEmitValue(message);
            }
        } else if ("event".equals(type)) {
            // 处理事件消息
            String route = message.getRoute();
            if (route != null) {
                String eventKey = url + ":" + route;
                Sinks.Many<Message<?>> eventStream = eventStreams.get(eventKey);
                if (eventStream != null) {
                    eventStream.tryEmitNext(message);
                }
            }
        }
        
        // 发送到通用响应流
        Sinks.Many<Message<?>> responseStream = responseStreams.get(url);
        if (responseStream != null) {
            responseStream.tryEmitNext(message);
        }
    }

    /**
     * 推送消息（不关心响应）
     */
    public Mono<Void> send(String url, Message<?> message) {
        WebSocketSession session = getSession(url);
        if (session == null) {
            return Mono.error(new IllegalArgumentException("Session not found for URL: " + url));
        }

        try {
            String json = objectMapper.writeValueAsString(message);
            return session.send(Mono.just(session.textMessage(json)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * 发送请求并等待响应
     */
    @SuppressWarnings("unchecked")
    public <RESP> Mono<RESP> sendRequest(String url, Message<?> request, Class<RESP> responseType) {
        // 生成消息ID
        String messageId = generateMessageId();
        request.setId(messageId);
        request.setType("request");

        // 创建响应等待器
        String requestKey = url + ":" + messageId;
        Sinks.One<Message<?>> responseSink = Sinks.one();
        pendingRequests.put(requestKey, responseSink);

        // 发送请求
        return send(url, request)
                .then(responseSink.asMono()
                        .timeout(Duration.ofSeconds(30)) // 30秒超时
                        .doFinally(signalType -> pendingRequests.remove(requestKey))
                        .map(responseMessage -> {
                            try {
                                if (responseMessage.getPayload() == null) {
                                    return null;
                                }
                                return objectMapper.convertValue(responseMessage.getPayload(), responseType);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to convert response payload", e);
                            }
                        }));
    }

    /**
     * 发送请求并订阅事件流
     */
    @SuppressWarnings("unchecked")
    public <Event> Flux<Event> sendAndSubscribe(String url, Message<?> request, Class<Event> eventType) {
        String route = request.getRoute();
        if (route == null) {
            return Flux.error(new IllegalArgumentException("Request route cannot be null for event subscription"));
        }

        // 创建事件流
        String eventKey = url + ":" + route;
        Sinks.Many<Message<?>> eventStream = Sinks.many().multicast().onBackpressureBuffer();
        eventStreams.put(eventKey, eventStream);

        // 发送订阅请求
        String messageId = generateMessageId();
        request.setId(messageId);
        request.setType("subscribe");

        return send(url, request)
                .thenMany(eventStream.asFlux()
                        .map(eventMessage -> {
                            try {
                                if (eventMessage.getPayload() == null) {
                                    return null;
                                }
                                return objectMapper.convertValue(eventMessage.getPayload(), eventType);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to convert event payload", e);
                            }
                        })
                        .doFinally(signalType -> {
                            // 清理事件流
                            eventStreams.remove(eventKey);
                            eventStream.tryEmitComplete();
                        }));
    }

    /**
     * 获取会话的消息流
     */
    public Flux<Message<?>> getMessageStream(String url) {
        Sinks.Many<Message<?>> responseStream = responseStreams.get(url);
        if (responseStream == null) {
            return Flux.empty();
        }
        return responseStream.asFlux();
    }

    /**
     * 生成唯一消息ID
     */
    private String generateMessageId() {
        return "msg-" + System.currentTimeMillis() + "-" + messageIdGenerator.incrementAndGet();
    }
}
