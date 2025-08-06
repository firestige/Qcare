package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.Message;

public interface MessageHandler {
    Mono<Void> handle(WebSocketSession session, Message<?> message);
}
