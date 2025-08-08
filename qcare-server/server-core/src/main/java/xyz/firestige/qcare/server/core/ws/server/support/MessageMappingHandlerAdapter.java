package xyz.firestige.qcare.server.core.ws.server.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import xyz.firestige.qcare.server.core.ws.server.DispatchExceptionHandler;
import xyz.firestige.qcare.server.core.ws.server.HandlerResult;
import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.method.ControllerMethodResolver;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethod;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethodArgumentResolver;
import xyz.firestige.qcare.server.core.ws.server.method.InvocableHandlerMethod;
import xyz.firestige.qcare.server.core.ws.server.HandlerAdapter;
import xyz.firestige.qcare.server.core.ws.server.method.annotation.MessagePayloadMethodArgumentResolver;

public class MessageMappingHandlerAdapter implements HandlerAdapter, DispatchExceptionHandler, ApplicationContextAware, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(MessageMappingHandlerAdapter.class);
    private ConfigurableApplicationContext ctx;
    private ControllerMethodResolver methodResolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        MessagePayloadExtractor extractor = ctx.getBean(MessagePayloadExtractor.class);
        resolvers.add(new MessagePayloadMethodArgumentResolver(extractor));
        this.methodResolver = new ControllerMethodResolver(resolvers);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext configurableApplicationContext) {
            this.ctx = configurableApplicationContext;
        }
    }

    @Override
    public Mono<HandlerResult> handleException(WsExchange exchange, Throwable exception) {
        // TODO: Implement exception handling logic
        return Mono.empty();
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public Mono<HandlerResult> handle(WsExchange exchange, Object handler) {
        HandlerMethod method = (HandlerMethod) handler;

        InvocableHandlerMethod invocableMethod = this.methodResolver.getInvocableHandlerMethod(method);
        DispatchExceptionHandler exceptionHandler = (s, ex) -> handleException(s.session(), ex, method);

        return invocableMethod.invoke(exchange)
                .doOnNext(result -> result.withExceptionHandler(exceptionHandler))
                .onErrorResume(ex -> exceptionHandler.handleException(exchange, ex));
    }

    private Mono<HandlerResult> handleException(WebSocketSession session, Throwable exception, HandlerMethod method) {
        // TODO: Implement exception handling logic
        return Mono.empty();
    }
}
