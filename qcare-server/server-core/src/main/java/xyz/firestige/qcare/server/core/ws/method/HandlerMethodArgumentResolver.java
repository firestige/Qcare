package xyz.firestige.qcare.server.core.ws.method;

import org.springframework.core.MethodParameter;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.WsExchange;

public interface HandlerMethodArgumentResolver {
    boolean supportsParameter(MethodParameter parameter);
    Mono<Object> resolveArgument(MethodParameter parameter, WsExchange exchange);
}
