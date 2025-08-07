package xyz.firestige.qcare.server.core.ws.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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
import xyz.firestige.qcare.server.core.ws.server.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.server.annotation.WebSocket;
import xyz.firestige.qcare.server.core.ws.server.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.link.AnnotationWebSocketHandler;
import xyz.firestige.qcare.server.core.ws.link.WebSocketLifeCircleAware;
import xyz.firestige.qcare.server.core.ws.link.WebSocketSessionManager;
import xyz.firestige.qcare.server.core.ws.server.mapping.WsMessageMappingHandlerMapping;
import xyz.firestige.qcare.server.core.ws.server.support.MessageMappingHandlerAdapter;

/**
 * WebSocket自动配置类
 */
@Configuration
@EnableWebFlux
public class WebSocketAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();

        Map<String, WebSocketLifeCircleAware> awareBeans = applicationContext.getBeansOfType(WebSocketLifeCircleAware.class);
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
        return new AnnotationWebSocketHandler(aware, sessionManager);
    }

    @Bean
    public WsMessageMappingHandlerMapping wsMessageMappingHandlerMapping() {
        WsMessageMappingHandlerMapping mapping = new WsMessageMappingHandlerMapping();
        mapping.setOrder(1); // 确保在WebSocket处理器之后执行
        mapping.setApplicationContext(applicationContext);
        return mapping;
    }

    @Bean
    public Dispatcher messageDispatcher(ApplicationContext ctx) {
        return new Dispatcher(ctx);
    }

    @Bean
    public MessageMappingHandlerAdapter messageMappingHandlerAdapter() {
        MessageMappingHandlerAdapter adapter = new MessageMappingHandlerAdapter();
        adapter.setApplicationContext(applicationContext);

        return adapter;
    }

}
