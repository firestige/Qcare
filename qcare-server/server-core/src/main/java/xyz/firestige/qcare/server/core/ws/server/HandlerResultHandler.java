package xyz.firestige.qcare.server.core.ws.server;

import reactor.core.publisher.Mono;

public interface HandlerResultHandler {
    boolean supports(HandlerResult result);
    Mono<Void> handleResult(WsExchange exchange, HandlerResult result);
}
