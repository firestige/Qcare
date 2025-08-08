package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;
import xyz.firestige.qcare.protocol.api.Message;

public interface WsExchange {
    WebSocketSession session();
    Message<?> message();
    String getAttribuString(String key);
    <T> T getAttribute(String key);
    <T> T getAttributeOrDefault(String key, T defaultValue);
    void setAttribute(String key, Object value);
}
