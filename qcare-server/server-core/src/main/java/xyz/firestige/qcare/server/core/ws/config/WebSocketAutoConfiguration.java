package xyz.firestige.qcare.server.core.ws.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.firestige.qcare.server.core.ws.MessageDispatcher;
import xyz.firestige.qcare.server.core.ws.annotation.MsgMapping;
import xyz.firestige.qcare.server.core.ws.annotation.OnClose;
import xyz.firestige.qcare.server.core.ws.annotation.OnError;
import xyz.firestige.qcare.server.core.ws.annotation.OnMessage;
import xyz.firestige.qcare.server.core.ws.annotation.OnOpen;
import xyz.firestige.qcare.server.core.ws.annotation.Websocket;
import xyz.firestige.qcare.server.core.ws.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.handler.AnnotationWebSocketHandler;
import xyz.firestige.qcare.server.core.ws.manager.WebSocketSessionManager;

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
        
        // 扫描所有被@Websocket注解的类
        Map<String, Object> websocketBeans = applicationContext.getBeansWithAnnotation(Websocket.class);
        
        for (Object bean : websocketBeans.values()) {
            Class<?> clazz = bean.getClass();
            Websocket websocketAnnotation = clazz.getAnnotation(Websocket.class);
            
            // 获取路径
            String[] paths = getPaths(websocketAnnotation);
            
            // 创建WebSocket处理器
            WebSocketHandler handler = createWebSocketHandler(bean);
            
            // 注册到映射中
            for (String path : paths) {
                map.put(path, handler);
            }
        }
        
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }

    @Bean
    public MessageDispatcher messageDispatcher() {
        List<xyz.firestige.qcare.server.core.ws.mapping.HandlerMapping> handlerMappings = new ArrayList<>();
        
        // 扫描所有被@WsMsgController注解的类
        Map<String, Object> controllerBeans = applicationContext.getBeansWithAnnotation(WsMsgController.class);
        
        for (Object bean : controllerBeans.values()) {
            Class<?> clazz = bean.getClass();
            MsgMapping classMapping = clazz.getAnnotation(MsgMapping.class);
            
            // 扫描方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                MsgMapping methodMapping = method.getAnnotation(MsgMapping.class);
                if (methodMapping != null) {
                    // 合并类级别和方法级别的映射
                    MsgMapping combinedMapping = combineMapping(classMapping, methodMapping);
                    handlerMappings.add(new xyz.firestige.qcare.server.core.ws.mapping.HandlerMapping(bean, method, combinedMapping));
                }
            }
        }
        
        return new MessageDispatcher(handlerMappings, objectMapper);
    }

    private String[] getPaths(Websocket annotation) {
        List<String> paths = new ArrayList<>();
        if (annotation.value().length > 0) {
            paths.addAll(Arrays.asList(annotation.value()));
        }
        if (annotation.path().length > 0) {
            paths.addAll(Arrays.asList(annotation.path()));
        }
        return paths.toArray(String[]::new);
    }

    private WebSocketHandler createWebSocketHandler(Object bean) {
        Class<?> clazz = bean.getClass();
        
        Method onOpenMethod = findMethodWithAnnotation(clazz, OnOpen.class);
        Method onMessageMethod = findMethodWithAnnotation(clazz, OnMessage.class);
        Method onErrorMethod = findMethodWithAnnotation(clazz, OnError.class);
        Method onCloseMethod = findMethodWithAnnotation(clazz, OnClose.class);
        
        return new AnnotationWebSocketHandler(bean, onOpenMethod, onMessageMethod, onErrorMethod, onCloseMethod, sessionManager);
    }

    private Method findMethodWithAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }
        return null;
    }

    private MsgMapping combineMapping(MsgMapping classMapping, MsgMapping methodMapping) {
        // 这里简化处理，实际应该创建一个代理对象
        return methodMapping != null ? methodMapping : classMapping;
    }
}
