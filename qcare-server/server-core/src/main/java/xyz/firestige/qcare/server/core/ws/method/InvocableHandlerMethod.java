package xyz.firestige.qcare.server.core.ws.method;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerResult;
import xyz.firestige.qcare.server.core.ws.Message;

public class InvocableHandlerMethod extends HandlerMethod {
    public Mono<HandlerResult> invoke(WebSocketSession session, Message<?> msg) {
        return Mono.empty();
    }
}
