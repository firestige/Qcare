package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerResult;
import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.WsExchange;

public interface HandlerAdapter {
    boolean supports(Object handler);
    Mono<HandlerResult> handle(WsExchange exchange, Object handler);
}
