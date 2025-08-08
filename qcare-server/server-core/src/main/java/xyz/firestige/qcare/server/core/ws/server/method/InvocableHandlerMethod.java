package xyz.firestige.qcare.server.core.ws.server.method;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.method.MethodValidator;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.HandlerResult;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class InvocableHandlerMethod extends HandlerMethod {
    private static final Mono<Object[]> EMPTY_ARGS = Mono.just(new Object[0]);
    private static final Class<?>[] EMPTY_GROUPS = new Class<?>[0];
    private static final Object NO_ARG_VALUE = new Object();

    private final HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();
    private ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
    private MethodValidator methodValidator;
    private Class<?>[] validationGroups = EMPTY_GROUPS;

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    public void setArgumentResolvers(List<? extends HandlerMethodArgumentResolver> resolvers) {
        this.resolvers.addResolvers(resolvers);
    }

    public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
        return this.resolvers.getResolvers();
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer discoverer) {
        this.discoverer = discoverer;
    }

    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return discoverer;
    }

    public void setMethodValidator(MethodValidator methodValidator) {
        this.methodValidator = methodValidator;
        this.validationGroups = Objects.nonNull(methodValidator)
                ? methodValidator.determineValidationGroups(getBean(), getBridgedMethod())
                : EMPTY_GROUPS;
    }

    /**
     * 调用处理器方法
     */
    public Mono<HandlerResult> invoke(WsExchange exchange, Object... args) {
        return resolveArguments(exchange, args).flatMap(this::doInvoke);
    }

    /**
     * 解析方法参数
     */
    private Mono<Object[]> resolveArguments(WsExchange exchange, Object... providedArgs) {
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        List<Mono<Object>> argMonos = new ArrayList<>(parameters.length);
        for (MethodParameter parameter : parameters) {
            parameter.initParameterNameDiscovery(this.discoverer);
            Object providedArg = findProvidedArgument(parameter, providedArgs);
            if (providedArg != null) {
                argMonos.add(Mono.just(providedArg));
                continue;
            }
            if (!resolvers.supportsParameter(parameter)) {
                return Mono.error(() -> new IllegalArgumentException("Parameter " + parameter.getParameterName() + " is not supported"));
            }
            Mono<Object> argMono = resolvers.resolveArgument(parameter, exchange).defaultIfEmpty(NO_ARG_VALUE);
            argMonos.add(argMono);
        }
        return Mono.zip(argMonos, values -> Stream.of(values).map(v -> v != NO_ARG_VALUE ? v : null).toArray());
    }

    /**
     * 执行方法调用
     */
    private Mono<HandlerResult> doInvoke(Object[] args) {
        if (isValidateArguments() && this.methodValidator != null) {
            this.methodValidator.applyArgumentValidation(getBean(), getBridgedMethod(), getMethodParameters(), args, this.validationGroups);
        }
        Method method = getBridgedMethod();
        Object value;
        try {
            value = method.invoke(getBean(), args);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return Mono.error(ex);
        }
        MethodParameter returnType = getReturnType();
        HandlerResult result = new HandlerResult(this, value, returnType);
        return Mono.just(result);
    }
}
