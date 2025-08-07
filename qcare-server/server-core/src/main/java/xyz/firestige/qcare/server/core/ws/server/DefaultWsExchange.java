package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class DefaultWsExchange implements WsExchange {

    private final WebSocketSession session;
    private final Message<?> message;
    private final Map<String, Object> attributes;

    public DefaultWsExchange(WebSocketSession session, Message<?> message) {
        this.session = session;
        this.message = message;
        this.attributes = new HashMap<>();
    }

    @Override
    public WebSocketSession session() {
        return session;
    }

    @Override
    public Message<?> message() {
        return message;
    }

    @Override
    public String getAttribuString(String key) {
        return attributes.get(key).toString();
    }

    @Override
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @Override
    public <T> T getAttributeOrDefault(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
