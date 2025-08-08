package xyz.firestige.qcare.server.core.ws.server.method.annotation;

import org.springframework.core.MethodParameter;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethodArgumentResolver;
import xyz.firestige.qcare.server.core.ws.server.support.MessagePayloadExtractor;

public class MessagePayloadMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final MessagePayloadExtractor extractor;

    public MessagePayloadMethodArgumentResolver(MessagePayloadExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MessagePayload.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, WsExchange exchange) {
        Object arg = extractor.extractPayload(exchange.message(), parameter);
        return Mono.just(arg);
    }
}
