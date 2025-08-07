package xyz.firestige.qcare.server.core.ws.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerMapping;
import xyz.firestige.qcare.server.core.ws.WsExchange;

import java.util.Collections;
import java.util.Objects;

public class RouteVariableArgumentResolver extends AbstractNamedValueArgumentResolver {
    public RouteVariableArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(RouteVariable.class)) {
            return false;
        }
        RouteVariable routeVariable = parameter.getParameterAnnotation(RouteVariable.class);
        return Objects.nonNull(routeVariable) && StringUtils.hasText(routeVariable.name());
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RouteVariable ann = parameter.getParameterAnnotation(RouteVariable.class);
        Objects.requireNonNull(ann, "No RouteVariable annotation found");
        return new RouteVariableNamedValueInfo(ann);
    }

    @Override
    protected Mono<Object> resolveName(String name, MethodParameter parameter, WsExchange exchange) {
        String attributeKey = HandlerMapping.URI_TEMPLATE;
        return Mono.justOrEmpty(exchange.getAttributeOrDefault(attributeKey, Collections.emptyMap()).get(name));
    }

    private static class RouteVariableNamedValueInfo extends NamedValueInfo {
        public RouteVariableNamedValueInfo(RouteVariable routeVariable) {
            super(routeVariable.name(), routeVariable.required(), ValueConstants.DEFAULT_NONE);
        }
    }
}
