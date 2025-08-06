package xyz.firestige.qcare.server.core.ws.example;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import xyz.firestige.qcare.server.core.ws.annotation.OnClose;
import xyz.firestige.qcare.server.core.ws.annotation.OnError;
import xyz.firestige.qcare.server.core.ws.annotation.OnMessage;
import xyz.firestige.qcare.server.core.ws.annotation.OnOpen;
import xyz.firestige.qcare.server.core.ws.annotation.Websocket;

/**
 * WebSocket处理器示例
 */
@Websocket("/ws/echo")
public class EchoWebSocketHandler {

    @OnOpen
    public void onOpen(WebSocketSession session) {
        System.out.println("WebSocket连接建立: " + session.getId());
    }

    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage message) {
        System.out.println("收到消息: " + message.getPayloadAsText());
        // 回显消息
        session.send(reactor.core.publisher.Mono.just(session.textMessage("Echo: " + message.getPayloadAsText()))).subscribe();
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
