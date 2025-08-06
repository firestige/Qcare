package xyz.firestige.qcare.server.core.ws.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.Dispatcher;
import xyz.firestige.qcare.server.core.ws.annotation.OnClose;
import xyz.firestige.qcare.server.core.ws.annotation.OnError;
import xyz.firestige.qcare.server.core.ws.annotation.OnMessage;
import xyz.firestige.qcare.server.core.ws.annotation.OnOpen;
import xyz.firestige.qcare.server.core.ws.annotation.Websocket;

/**
 * 消息路由WebSocket处理器
 */
@Websocket("/ws/message")
public class MessageRoutingWebSocketHandler {

    @Autowired
    private Dispatcher dispatcher;
    
    @Autowired
    private ObjectMapper objectMapper;

    @OnOpen
    public void onOpen(WebSocketSession session) {
        System.out.println("消息路由WebSocket连接建立: " + session.getId());
    }

    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String messageText = message.getPayloadAsText();
            System.out.println("收到消息: " + messageText);
            
            // 使用消息分发器处理消息
            Message<?> response = dispatcher.handleMessage(messageText, session);
            
            if (response != null) {
                // 发送响应
                String responseJson = objectMapper.writeValueAsString(response);
                session.send(Mono.just(session.textMessage(responseJson))).subscribe();
            }
            
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
            
            // 发送错误响应
            try {
                Message<String> errorResponse = new Message<>();
                errorResponse.setType("error");
                errorResponse.setPayload("处理消息时发生错误: " + e.getMessage());
                
                String errorJson = objectMapper.writeValueAsString(errorResponse);
                session.send(Mono.just(session.textMessage(errorJson))).subscribe();
            } catch (Exception ex) {
                System.err.println("发送错误响应失败: " + ex.getMessage());
            }
        }
    }

    @OnError
    public void onError(WebSocketSession session, Throwable error) {
        System.err.println("WebSocket错误: " + error.getMessage());
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        System.out.println("WebSocket连接关闭: " + session.getId());
    }
}
