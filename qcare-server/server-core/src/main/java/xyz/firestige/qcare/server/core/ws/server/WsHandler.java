package xyz.firestige.qcare.server.core.ws.server;

import reactor.core.publisher.Mono;

public interface WsHandler {
    Mono<Void> handle(WsExchange wsExchange);
}
