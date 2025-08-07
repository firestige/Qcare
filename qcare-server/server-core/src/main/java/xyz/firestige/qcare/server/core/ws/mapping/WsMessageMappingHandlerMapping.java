package xyz.firestige.qcare.server.core.ws.mapping;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerMapping;
import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.WsExchange;
import xyz.firestige.qcare.server.core.ws.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.method.HandlerMethod;
import xyz.firestige.qcare.server.core.ws.server.WsHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WsMessageMappingHandlerMapping extends WsMessageMappingInfoHandlerMapping {

    private final Map<String, HandlerMethod> handlerMethods = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        initHandlerMethods();
    }

    @Override
    public Mono<Object> getHandler(WsExchange exchange) {
        return Mono.justOrEmpty(lookupHandlerMethod(exchange.message()));
    }

    /**
     * 注册处理器方法
     */
    private void registerHandlerMethod(Object controller, Method method, RouteMapping mapping) {
        String[] routes = mapping.route();
        if (routes.length == 0) {
            routes = mapping.value();
        }

        for (String route : routes) {
            if (StringUtils.hasText(route)) {
                HandlerMethod handlerMethod = createHandlerMethod(controller, method);
                handlerMethods.put(route, handlerMethod);

                if (logger.isDebugEnabled()) {
                    logger.debug("Mapped route [" + route + "] onto " + handlerMethod);
                }
            }
        }
    }

    /**
     * 创建HandlerMethod实例
     */
    private HandlerMethod createHandlerMethod(Object controller, Method method) {
        return new HandlerMethod(controller, method);
    }

    /**
     * 查找处理器方法
     */
    private HandlerMethod lookupHandlerMethod(Message<?> message) {
        String route = message.getRoute();
        if (!StringUtils.hasText(route)) {
            return null;
        }

        return handlerMethods.get(route);
    }
}
