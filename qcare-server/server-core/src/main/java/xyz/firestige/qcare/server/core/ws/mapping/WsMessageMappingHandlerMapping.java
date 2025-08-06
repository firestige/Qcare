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
import xyz.firestige.qcare.server.core.ws.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WsMessageMappingHandlerMapping extends ApplicationObjectSupport implements HandlerMapping, InitializingBean {

    private final Map<String, HandlerMethod> handlerMethods = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        initHandlerMethods();
    }

    @Override
    public Mono<Object> getHandler(Message<?> msg) {
        return Mono.justOrEmpty(lookupHandlerMethod(msg));
    }

    /**
     * 初始化处理器方法映射
     */
    private void initHandlerMethods() {
        ApplicationContext context = getApplicationContext();
        if (context == null) {
            return;
        }

        // 获取所有标记了@WsMsgController的bean
        String[] controllerNames = context.getBeanNamesForAnnotation(WsMsgController.class);

        for (String controllerName : controllerNames) {
            Object controller = context.getBean(controllerName);
            Class<?> controllerType = controller.getClass();

            // 扫描控制器中的处理方法
            Map<Method, RouteMapping> methods = MethodIntrospector.selectMethods(controllerType,
                (MethodIntrospector.MetadataLookup<RouteMapping>) method ->
                    AnnotatedElementUtils.findMergedAnnotation(method, RouteMapping.class));

            methods.forEach((method, mapping) -> {
                registerHandlerMethod(controller, method, mapping);
            });
        }
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

    /**
     * 获取所有注册的路由映射
     */
    public Map<String, HandlerMethod> getHandlerMethods() {
        return new ConcurrentHashMap<>(handlerMethods);
    }
}
