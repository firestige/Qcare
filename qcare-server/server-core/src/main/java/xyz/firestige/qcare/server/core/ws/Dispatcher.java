package xyz.firestige.qcare.server.core.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.HandlerAdapter;
import xyz.firestige.qcare.server.core.ws.server.HandlerResultHandler;
import xyz.firestige.qcare.server.core.ws.server.MessageHandler;

/**
 * 消息分发器，类似于DispatcherHandler
 */
public class Dispatcher implements MessageHandler, ApplicationContextAware {

    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;
    private List<HandlerResultHandler> resultHandlers;

    public Dispatcher(ApplicationContext ctx) {
        init(ctx);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        init(applicationContext);
    }

    private void init(ApplicationContext ctx) {
        Map<String, HandlerMapping> mappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx, HandlerMapping.class, true, false);
        ArrayList<HandlerMapping> mappings = new ArrayList<>(mappingBeans.values());
        AnnotationAwareOrderComparator.sort(mappings);
        this.handlerMappings = Collections.unmodifiableList(mappings);
        Map<String, HandlerAdapter> adapterBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx, HandlerAdapter.class, true, false);
        ArrayList<HandlerAdapter> adapters = new ArrayList<>(adapterBeans.values());
        AnnotationAwareOrderComparator.sort(adapters);
        this.handlerAdapters = Collections.unmodifiableList(adapters);
        Map<String, HandlerResultHandler> resultHandlerBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx, HandlerResultHandler.class, true, false);
        ArrayList<HandlerResultHandler> beans = new ArrayList<>(resultHandlerBeans.values());
        AnnotationAwareOrderComparator.sort(beans);
        this.resultHandlers = Collections.unmodifiableList(beans);
    }

    @Override
    public Mono<Void> handle(WebSocketSession session, Message<?> message) {
        return Flux.fromIterable(this.handlerMappings)
                .concatMap(mapping -> mapping.getHandler(message))
                .next()
                .switchIfEmpty(createNotFoundError())
                .onErrorResume(e -> handleResultMono(session, Mono.error(e)))
                .flatMap(handler -> handleMessageWith(message, session, handler));
    }



    private <R> Mono<R> createNotFoundError() {
        return Mono.defer(() -> Mono.error(new RuntimeException("No handler found for message")));
    }

    private Mono<Void> handleMessageWith(Message<?> message, WebSocketSession session, Object handler) {
        for (HandlerAdapter adapter : this.handlerAdapters) {
            if (adapter.supports(handler)) {
                Mono<HandlerResult> resultMono = adapter.handle(message, session, handler);
                return handleResultMono(session, resultMono);
            }
        }
        return Mono.error(new RuntimeException("No handler adapter found for " + handler.getClass().getName()));
    }

    private Mono<Void> handleResultMono(WebSocketSession session, Mono<HandlerResult> resultMono) {
        for (HandlerAdapter adapter : this.handlerAdapters) {
            if (adapter instanceof DispatchExceptionHandler exceptionHandler) {
                resultMono = resultMono.onErrorResume(e -> exceptionHandler.handleException(session, e));
            }
        }
        return resultMono.flatMap(result -> {
            Mono<Void> voidMono = handleResult(session, result, "Handler" + result.getHandler());
            if (result.getExceptionHandler() != null) {
                voidMono = voidMono.onErrorResume(e -> 
                        result.getExceptionHandler().handleException(session, e).flatMap(r ->
                                handleResult(session, r, "ExceptionHandler" + r.getHandler() + ", error=\"" + e.getMessage() + "\"")));
            }
            return voidMono;
        });
    }

    private Mono<Void> handleResult(WebSocketSession session, HandlerResult result, String desc) {
        for (HandlerResultHandler handler : this.resultHandlers) {
            if (handler.supports(result)) {
                desc += "[DispatcherHandler]";
                return handler.handleResult(session, result).checkpoint(desc);
            }
        }
        return Mono.error(new RuntimeException("No result handler found for " + result.getReturnValue()));
    }
}
