package xyz.firestige.qcare.server.core.ws.handler;

public interface LifeCycleWebSocketHandler {
    Mono<Void> onOpen(WebSocketSession session);
    Mono<Void> onMessage(WebSocketSession session, WebSocketMessage message);
    Mono<Void> onError(WebSocketSession session, Throwable error);
    Mono<Void> onClose(WebSocketSession session);
}
