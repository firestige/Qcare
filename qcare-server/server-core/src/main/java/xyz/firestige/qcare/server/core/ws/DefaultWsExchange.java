package xyz.firestige.qcare.server.core.ws;

import org.springframework.web.reactive.socket.WebSocketSession;

public class DefaultWsExchange implements WsExchange {

    private final WebSocketSession session;
    private final Message<?> message;
    public DefaultWsExchange(WebSocketSession session, Message<?> message) {
        this.session = session;
        this.message = message;
    }

    @Override
    public WebSocketSession session() {
        return session;
    }

    @Override
    public Message<?> message() {
        return message;
    }
}
