package xyz.firestige.qcare.server.core.ws.server;

import reactor.core.publisher.Mono;

public interface DispatchExceptionHandler {
    Mono<HandlerResult> handleException(WsExchange exchange, Throwable exception);
}
