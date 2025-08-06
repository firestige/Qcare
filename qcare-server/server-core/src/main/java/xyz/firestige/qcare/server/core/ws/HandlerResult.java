package xyz.firestige.qcare.server.core.ws;

import java.util.function.Function;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.method.HandlerMethod;

public class HandlerResult {
    private final Object handler;
    private final Object returnValue;
    private final ResolvableType returnType;
    private final Message<?> requestMessage;
    private DispatchExceptionHandler exceptionHandler;
    private Function<Throwable, Mono<HandlerResult>> exceptionHandlerFunction;

    public HandlerResult(Object handler, Object returnValue, MethodParameter returnType) {
        this.handler = handler;
        this.returnValue = returnValue;
        this.returnType = ResolvableType.forMethodParameter(returnType);
        this.requestMessage = null;
    }

    /**
     * WebSocket消息处理专用构造函数
     */
    public HandlerResult(HandlerMethod handlerMethod, Object returnValue, Message<?> requestMessage) {
        this.handler = handlerMethod;
        this.returnValue = returnValue;
        this.returnType = ResolvableType.forMethodParameter(handlerMethod.getReturnType());
        this.requestMessage = requestMessage;
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

    public Message<?> getRequestMessage() {
        return requestMessage;
    }

    public HandlerResult withExceptionHandler(DispatchExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public DispatchExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
