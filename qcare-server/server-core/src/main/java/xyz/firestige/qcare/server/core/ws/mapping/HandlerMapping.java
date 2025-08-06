package xyz.firestige.qcare.server.core.ws.mapping;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.annotation.MsgMapping;

/**
 * 处理器映射信息
 */
public class HandlerMapping {
    private final Object handler;
    private final Method method;
    private final MsgMapping mapping;
    private final List<String> routes;
    private final List<String> types;
    private final List<String> actions;

    public HandlerMapping(Object handler, Method method, MsgMapping mapping) {
        this.handler = handler;
        this.method = method;
        this.mapping = mapping;
        this.routes = getRoutes(mapping);
        this.types = getTypes(mapping);
        this.actions = getActions(mapping);
    }

    private List<String> getRoutes(MsgMapping mapping) {
        List<String> routeList = new java.util.ArrayList<>();
        if (mapping.value().length > 0) {
            routeList.addAll(java.util.Arrays.asList(mapping.value()));
        }
        if (mapping.route().length > 0) {
            routeList.addAll(java.util.Arrays.asList(mapping.route()));
        }
        return routeList;
    }

    private List<String> getTypes(MsgMapping mapping) {
        return java.util.Arrays.asList(mapping.type());
    }

    private List<String> getActions(MsgMapping mapping) {
        return java.util.Arrays.asList(mapping.action());
    }

    public boolean matches(Message<?> message) {
        // 检查路由匹配
        if (!routes.isEmpty() && !matchesRoute(message.getRoute())) {
            return false;
        }
        
        // 检查类型匹配
        if (!types.isEmpty() && !types.contains(message.getType())) {
            return false;
        }
        
        // 检查动作匹配
        if (!actions.isEmpty() && message.getAction() != null && !actions.contains(message.getAction())) {
            return false;
        }
        
        return true;
    }

    private boolean matchesRoute(String route) {
        if (route == null) {
            return false;
        }
        
        for (String pattern : routes) {
            if (pattern.equals(route) || isPatternMatch(pattern, route)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPatternMatch(String pattern, String route) {
        // 简单的通配符匹配，支持 * 和 **
        if (pattern.contains("*")) {
            String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
            return route.matches(regex);
        }
        return false;
    }

    public Mono<Object> getHandler(WebSocketSession session) {
        return Mono.just(handler);
    }

    public Method getMethod() {
        return method;
    }

    public MsgMapping getMapping() {
        return mapping;
    }
}
