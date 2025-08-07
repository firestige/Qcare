package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerResult;
import xyz.firestige.qcare.server.core.ws.WsExchange;

public interface HandlerResultHandler {
    boolean supports(HandlerResult result);
    Mono<Void> handleResult(WsExchange exchange, HandlerResult result);
}
