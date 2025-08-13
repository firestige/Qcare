package xyz.firestige.qcare.server.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 端点
 * 处理WebSocket连接和消息
 */
@ServerEndpoint("/ws")
@ApplicationScoped
public class ProxyWebSocketEndpoint {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket connection opened: " + session.getId());

        try {
            session.getBasicRemote().sendText("{\"type\":\"connected\",\"sessionId\":\"" + session.getId() + "\"}");
        } catch (IOException e) {
            System.err.println("Error sending welcome message: " + e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);

        try {
            // Echo the message back for now
            session.getBasicRemote().sendText("{\"type\":\"echo\",\"data\":\"" + message + "\"}");
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session.getId());
        System.out.println("WebSocket connection closed: " + session.getId() +
                          ", reason: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * 广播消息给所有连接的客户端
     */
    public void broadcast(String message) {
        sessions.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.err.println("Error broadcasting message to " + session.getId() + ": " + e.getMessage());
            }
        });
    }

    /**
     * 发送消息给指定的会话
     */
    public void sendToSession(String sessionId, String message) {
        Session session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.err.println("Error sending message to " + sessionId + ": " + e.getMessage());
            }
        }
    }
}
