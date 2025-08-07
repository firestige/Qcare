package xyz.firestige.qcare.server.core.ws.server;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

public class WsResponseResultHandler implements HandlerResultHandler, InitializingBean, Ordered {

    private final  ObjectMapper objectMapper;

    private int order = 0;

    public WsResponseResultHandler(ObjectMapper objectMapper) {
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
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public boolean supports(HandlerResult result) {
        return result.getReturnValue() instanceof WsResponse;
    }

    @Override
    public Mono<Void> handleResult(WebSocketSession session, HandlerResult result) {
        Mono<WebSocketMessage> messageMono = Mono.just(result)
                .map(HandlerResult::getReturnValue)
                .cast(WsResponse.class)
                .flatMap(resp -> {
                    try {
                        String json = objectMapper.writeValueAsString(resp);
                        return Mono.just(session.textMessage(json));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
        return session.send(messageMono);
    }
}
