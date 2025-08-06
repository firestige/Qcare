package xyz.firestige.qcare.server.core.ws;

import java.util.function.Function;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import reactor.core.publisher.Mono;

public class HandlerResult {
    private final Object handler;
    private final Object returnValue;
    private final ResolvableType returnType;
    private DispatchExceptionHandler exceptionHandler;
    private Function<Throwable, Mono<HandlerResult>> exceptionHandlerFunction;

    public HandlerResult(Object handler, Object returnValue, MethodParameter returnType) {
        this.handler = handler;
        this.returnValue = returnValue;
        this.returnType = ResolvableType.forMethodParameter(returnType);
    }
    // Getters and setters
    public Object getHandler() {
        return handler;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public ResolvableType getReturnType() {
        return returnType;
    }

    public MethodParameter getReturnTypeParameter() {
        return (MethodParameter) this.returnType.getSource();
    }

    public HandlerResult withExceptionHandler(DispatchExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public DispatchExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
