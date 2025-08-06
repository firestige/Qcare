package xyz.firestige.qcare.server.core.ws.server.support;

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import xyz.firestige.qcare.server.core.ws.DispatchExceptionHandler;
import xyz.firestige.qcare.server.core.ws.HandlerResult;
import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.method.ControllerMethodResolver;
import xyz.firestige.qcare.server.core.ws.method.HandlerMethod;
import xyz.firestige.qcare.server.core.ws.method.InvocableHandlerMethod;
import xyz.firestige.qcare.server.core.ws.server.HandlerAdapter;

public class MessageMappingHandlerAdapter implements HandlerAdapter, DispatchExceptionHandler, ApplicationContextAware, InitializingBean {
    private ArgumentResolverConfigurer argumentResolverConfigurer;
    private Scheduler scheduler;
    private ReactiveAdapterRegistry reactiveAdapterRegistry;
    private ConfigurableApplicationContext ctx;
    private ControllerMethodResolver methodResolver;



    @Override
    public void afterPropertiesSet() throws Exception {
        this.argumentResolverConfigurer = Optional.ofNullable(this.argumentResolverConfigurer)
                .orElseGet(ArgumentResolverConfigurer::new);
        this.reactiveAdapterRegistry = Optional.ofNullable(this.reactiveAdapterRegistry)
                .orElseGet(ReactiveAdapterRegistry::getSharedInstance);
        this.methodResolver = new ControllerMethodResolver();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
            this.ctx = configurableApplicationContext;
        }
    }

    @Override
    public Mono<HandlerResult> handleException(WebSocketSession session, Throwable exception) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleException'");
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public Mono<HandlerResult> handle(Message<?> message, WebSocketSession session, Object handler) {
        HandlerMethod method = (HandlerMethod) handler;

        InvocableHandlerMethod invocableMethod = this.methodResolver.getInvocableHandlerMethod(method);
        DispatchExceptionHandler exceptionHandler = (s, ex) -> handleException(s, ex, method);

        Mono<HandlerResult> resultMono = invocableMethod.invoke(session, message)
                .doOnNext(result -> result.withExceptionHandler(exceptionHandler))
                .onErrorResume(ex -> exceptionHandler.handleException(session, ex));
        return resultMono;
    }

    private Mono<HandlerResult> handleException(WebSocketSession session, Throwable exception, HandlerMethod method) {
        // 处理异常逻辑
        return Mono.error(exception);
    }
}
