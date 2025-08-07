package xyz.firestige.qcare.server.core.ws.link;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public interface WebSocketLifeCircleAware {
    Mono<Void> onOpen(WebSocketSession session);
    Mono<Void> onMessage(WebSocketSession session, WebSocketMessage message);
    void onError(Throwable error, WebSocketSession session);
    void onClose(WebSocketSession session);
}
