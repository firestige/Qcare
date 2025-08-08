package xyz.firestige.qcare.server.core.ws.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.firestige.qcare.server.core.ws.server.Dispatcher;
import xyz.firestige.qcare.server.core.ws.server.support.MessagePayloadResultHandler;
import xyz.firestige.qcare.server.core.ws.server.support.WsMessageResultHandler;
import xyz.firestige.qcare.server.core.ws.server.annotation.WebSocket;
import xyz.firestige.qcare.server.core.ws.link.AnnotationWebSocketHandler;
import xyz.firestige.qcare.server.core.ws.link.WebSocketLifeCircleAware;
import xyz.firestige.qcare.server.core.ws.server.mapping.WsMessageMappingHandlerMapping;
import xyz.firestige.qcare.server.core.ws.server.support.MessageMappingHandlerAdapter;

/**
 * WebSocket自动配置类
 */
@Configuration
@EnableWebFlux
public class WebSocketAutoConfiguration {

    @Bean
    public HandlerMapping webSocketHandlerMapping(ApplicationContext ctx) {
        Map<String, WebSocketHandler> map = new HashMap<>();

        Map<String, WebSocketLifeCircleAware> awareBeans = ctx.getBeansOfType(WebSocketLifeCircleAware.class);
        for (WebSocketLifeCircleAware aware : awareBeans.values()) {
            Class<?> clazz = aware.getClass();
            WebSocket websocketAnnotation = clazz.getAnnotation(WebSocket.class);
            if (Objects.nonNull(websocketAnnotation)) {
                String path = websocketAnnotation.path();
                if (StringUtils.hasText(path)) {
                    map.put(path, createWebSocketHandler(aware));
                }
            }
        }
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(0);
        return mapping;
    }

    private WebSocketHandler createWebSocketHandler(WebSocketLifeCircleAware aware) {
        return new AnnotationWebSocketHandler(aware);
    }

    @Bean
    public WsMessageMappingHandlerMapping wsMessageMappingHandlerMapping(ApplicationContext ctx) {
        WsMessageMappingHandlerMapping mapping = new WsMessageMappingHandlerMapping();
        mapping.setOrder(1); // 确保在WebSocket处理器之后执行
        mapping.setApplicationContext(ctx);
        return mapping;
    }

    @Bean("dispatcher")
    public Dispatcher dispatcher(ApplicationContext ctx) {
        return new Dispatcher(ctx);
    }

    @Bean
    public MessageMappingHandlerAdapter messageMappingHandlerAdapter(ApplicationContext ctx) {
        MessageMappingHandlerAdapter adapter = new MessageMappingHandlerAdapter();
        adapter.setApplicationContext(ctx);

        return adapter;
    }

    @Bean
    public WsMessageResultHandler wsMessageResultHandler(ObjectMapper objectMapper) {
        return new WsMessageResultHandler(objectMapper);
    }

    @Bean
    public MessagePayloadResultHandler messagePayloadResultHandler(ObjectMapper objectMapper) {
        return new MessagePayloadResultHandler(objectMapper);
    }

}
