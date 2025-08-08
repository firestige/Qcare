package xyz.firestige.qcare.server.core.ws.server.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.socket.WebSocketMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.HandlerResult;
import xyz.firestige.qcare.server.core.ws.server.HandlerResultHandler;
import xyz.firestige.qcare.protocol.api.Message;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;

public class WsMessageResultHandler implements HandlerResultHandler, InitializingBean, Ordered {

    private final  ObjectMapper objectMapper;

    private int order = 0;

    public WsMessageResultHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public boolean supports(HandlerResult result) {
        return result.getReturnValue() instanceof Message<?>;
    }

    @Override
    public Mono<Void> handleResult(WsExchange exchange, HandlerResult result) {
        Mono<WebSocketMessage> messageMono = Mono.just(result)
                .map(HandlerResult::getReturnValue)
                .cast(Message.class)
                .flatMap(resp -> {
                    try {
                        String json = objectMapper.writeValueAsString(resp);
                        return Mono.just(exchange.session().textMessage(json));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
        return exchange.session().send(messageMono);
    }
}
