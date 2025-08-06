package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerResult;

public interface HandlerResultHandler {
    boolean supports(HandlerResult result);
    Mono<Void> handleResult(WebSocketSession session, Mono<HandlerResult> result);
}
