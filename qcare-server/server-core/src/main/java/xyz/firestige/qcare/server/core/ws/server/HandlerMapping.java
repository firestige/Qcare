package xyz.firestige.qcare.server.core.ws.server;

import reactor.core.publisher.Mono;

public interface HandlerMapping {
    String URI_TEMPLATE = "uriTemplate";

    Mono<Object> getHandler(WsExchange exchange);
}
