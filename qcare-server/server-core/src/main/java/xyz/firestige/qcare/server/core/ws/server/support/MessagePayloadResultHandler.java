package xyz.firestige.qcare.server.core.ws.server.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.HandlerResult;
import xyz.firestige.qcare.server.core.ws.server.HandlerResultHandler;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.annotation.ResponseMessage;

public class MessagePayloadResultHandler implements HandlerResultHandler, InitializingBean, Ordered {
    private final ObjectMapper objectMapper;
    private int order = Ordered.LOWEST_PRECEDENCE;

    public MessagePayloadResultHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public boolean supports(HandlerResult result) {
        MethodParameter returnType = result.getReturnTypeSource();
        Class<?> containingClass = returnType.getContainingClass();
        return AnnotatedElementUtils.hasAnnotation(containingClass, ResponseMessage.class)
                || returnType.hasMethodAnnotation(ResponseMessage.class);
    }

    @Override
    public Mono<Void> handleResult(WsExchange exchange, HandlerResult result) {
        // TODO 还是需要ReactiveAdapter，这里如果result是publisher包裹的就不能直接用
        Mono<?> resultMono = (Mono<?>) result.getReturnValue();
        Mono<WebSocketMessage> webSocketMessageMono = resultMono.map(payload -> exchange.message().createResponse(payload))
                .flatMap(msg -> {
                    try {
                        String jsonStr = objectMapper.writeValueAsString(msg);
                        return Mono.just(exchange.session().textMessage(jsonStr));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
        return exchange.session().send(webSocketMessageMono);
    }
}