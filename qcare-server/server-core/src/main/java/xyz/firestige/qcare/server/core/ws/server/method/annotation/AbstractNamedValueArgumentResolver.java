package xyz.firestige.qcare.server.core.ws.server.method.annotation;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.server.MissingRequestValueException;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethodArgumentResolver;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractNamedValueArgumentResolver implements HandlerMethodArgumentResolver {
    private final ConfigurableBeanFactory beanFactory;
    private final BeanExpressionContext expressionContext;
    private final Map<MethodParameter, NamedValueInfo> namedValueInfoMap = new ConcurrentHashMap<>();

    public AbstractNamedValueArgumentResolver(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.expressionContext = Objects.nonNull(beanFactory) ? new BeanExpressionContext(beanFactory, null) : null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, WsExchange exchange) {
        NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
        MethodParameter nestedParameter = parameter.nestedIfOptional();

        Object resolveName = resolveEmbeddedValuesAndExpressions(namedValueInfo.name);
        if (Objects.isNull(resolveName)) {
            return Mono.error(new IllegalArgumentException("Specified name must not be null["+namedValueInfo.name+"]"));
        }

        return resolveName(resolveName.toString(), nestedParameter, exchange)
                .flatMap(arg -> {
                    if ("".equals(arg) && Objects.nonNull(namedValueInfo.defaultValue)) {
                        arg = resolveEmbeddedValuesAndExpressions(namedValueInfo.defaultValue);
                    }
                    arg = applyConversion(arg, namedValueInfo, parameter, exchange);
                    handleResolvedValue(arg, namedValueInfo.name, parameter, exchange);
                    return Mono.justOrEmpty(arg);
                })
                .switchIfEmpty(getDefaultValue(namedValueInfo, resolveName.toString(), parameter, exchange));
    }

    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        return namedValueInfoMap.computeIfAbsent(parameter, k -> {
            NamedValueInfo info = createNamedValueInfo(parameter);
            info = updateNamedValueInfo(parameter, info);
            return info;
        });
    }

    protected abstract NamedValueInfo createNamedValueInfo(MethodParameter parameter);

    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.isEmpty()) {
            name = Objects.requireNonNull(parameter.getParameterName(), "Name must not be empty["+name+"]");
        }
        String defaultValue = ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue;
        return new NamedValueInfo(name, info.required, defaultValue);
    }

    private Object resolveEmbeddedValuesAndExpressions(String s) {
        if (Objects.isNull(beanFactory)||Objects.isNull(expressionContext)) {
            return s;
        }
        String placeholdersResolved = this.beanFactory.resolveEmbeddedValue(s);
        BeanExpressionResolver exprResolver = this.beanFactory.getBeanExpressionResolver();
        if (Objects.isNull(exprResolver)) {
            return s;
        }
        return exprResolver.evaluate(placeholdersResolved, expressionContext);
    }

    protected abstract Mono<Object> resolveName(String name, MethodParameter parameter, WsExchange exchange);

    protected Object applyConversion(@Nullable Object value, NamedValueInfo name, MethodParameter parameter, WsExchange exchange) {
        if (value == null) {
            return null;
        }
        Class<?> targetType = parameter.getParameterType();
        if (targetType.isInstance(value)) {
            return value;
        }
        String strValue = value.toString();
        if (targetType == String.class) {
            return strValue;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(strValue);
        }
        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(strValue);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(strValue);
        }
        // 其他类型可通过 beanFactory 或 ConversionService 扩展
        if (this.beanFactory != null && this.beanFactory.getConversionService() != null) {
            return this.beanFactory.getConversionService().convert(value, targetType);
        }
        // 默认返回字符串
        return strValue;
    }

    private Mono<Object> getDefaultValue(NamedValueInfo namedValueInfo, String resolvedName,MethodParameter parameter, WsExchange exchange) {
        return Mono.fromSupplier(() -> {
            Object value = null;
            if (Objects.nonNull(namedValueInfo.defaultValue)) {
                value = resolveEmbeddedValuesAndExpressions(namedValueInfo.defaultValue);
            } else if (namedValueInfo.required && !parameter.isOptional()) {
                handleMissingValue(resolvedName, parameter, exchange);
            }
            value = handleNullValue(resolvedName, value, parameter.getNestedParameterType());
            if (Objects.isNull(value)) {
                value = applyConversion(value, namedValueInfo, parameter, exchange);
            }
            handleResolvedValue(value, namedValueInfo.name, parameter, exchange);
            return value;
        });
    }

    protected void handleMissingValue(String resolvedName, MethodParameter parameter, WsExchange exchange) {
        handleMissingValue(resolvedName, parameter);
    }

    protected void handleMissingValue(String name, MethodParameter parameter) {
        throw new MissingRequestValueException(name, parameter.getNestedParameterType(), "request value", parameter);
    }

    private Object handleNullValue(String name, @Nullable Object value, Class<?> paramType) {
        if (Objects.isNull(value)) {
            if (paramType == boolean.class || paramType == Boolean.class) {
                return Boolean.FALSE;
            } else if (paramType.isPrimitive()) {
                throw new IllegalStateException(String.format("Parameter type [%s] is not primitive[%s]", paramType, paramType));
            }
        }
        return value;
    }

    private void handleResolvedValue(Object arg, String name, MethodParameter parameter, WsExchange exchange) {}

    protected static class NamedValueInfo {
        private final String name;
        private final String defaultValue;
        private final boolean required;

        public NamedValueInfo(String name, boolean required, @Nullable String defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}
