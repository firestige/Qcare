package xyz.firestige.qcare.server.core.ws.method;

import org.springframework.core.MethodParameter;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.WsExchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
    private final Map<MethodParameter, HandlerMethodArgumentResolver> resolverMap = new HashMap<>();

    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
        resolvers.add(resolver);
        return this;
    }

    public void addResolvers(List<? extends HandlerMethodArgumentResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    public List<HandlerMethodArgumentResolver> getResolvers() {
        return this.resolvers;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, WsExchange exchange) {
        HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            return Mono.error(new IllegalArgumentException("Unsupported parameter type [" + parameter.getParameterType().getName() + "]. supportsParameter should be called first."));
        }
        return resolver.resolveArgument(parameter, exchange);
    }

    private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        HandlerMethodArgumentResolver result = this.resolverMap.get(parameter);
        if (result == null) {
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                if (resolver.supportsParameter(parameter)) {
                    this.resolverMap.put(parameter, resolver);
                    return resolver;
                }
            }
        }
        return result;
    }
}
