package xyz.firestige.qcare.server.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebsocketConfig {
    @Bean
    public HandlerMapping webSocketHandlerMapping(ApplicationContext ctx) {
        Map<String, WebSocketHandler> beans = ctx.getBeansOfType(WebSocketHandler.class, false, true);
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/ws/agent/{agentId}", beans.get("agentWebSocketHandler"));
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(1);
        return mapping;
    }
}
