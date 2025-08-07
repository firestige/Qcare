package xyz.firestige.qcare.server.core.ws.server.method;

import org.springframework.core.MethodParameter;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;

public interface HandlerMethodArgumentResolver {
    boolean supportsParameter(MethodParameter parameter);
    Mono<Object> resolveArgument(MethodParameter parameter, WsExchange exchange);
}
