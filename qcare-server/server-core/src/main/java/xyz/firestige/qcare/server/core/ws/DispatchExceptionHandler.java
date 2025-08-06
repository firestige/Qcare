package xyz.firestige.qcare.server.core.ws;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

public interface DispatchExceptionHandler {
    Mono<HandlerResult> handleException(WebSocketSession session, Throwable exception);
}
