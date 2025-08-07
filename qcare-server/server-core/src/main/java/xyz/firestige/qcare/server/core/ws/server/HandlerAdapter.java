package xyz.firestige.qcare.server.core.ws.server;

import reactor.core.publisher.Mono;

public interface HandlerAdapter {
    boolean supports(Object handler);
    Mono<HandlerResult> handle(WsExchange exchange, Object handler);
}
