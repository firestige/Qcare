package xyz.firestige.qcare.server.core.ws;

import reactor.core.publisher.Mono;

public interface HandlerMapping {
    Mono<Object> getHandler(Message<?> message);
}
