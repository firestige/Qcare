package xyz.firestige.qcare.server.core.ws.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息分发器，类似于DispatcherHandler
 */
public class Dispatcher implements WsHandler, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
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
    public Mono<Void> handle(WsExchange exchange) {
        return Flux.fromIterable(this.handlerMappings)
                .concatMap(mapping -> mapping.getHandler(exchange))
                .next()
                .doOnNext(o -> log.info("found handler: {}", o.getClass().getSimpleName()))
                .switchIfEmpty(createNotFoundError())
                .onErrorResume(e -> handleResultMono(exchange, Mono.error(e)))
                .flatMap(handler -> handleMessageWith(exchange, handler));
    }



    private <R> Mono<R> createNotFoundError() {
        return Mono.defer(() -> Mono.error(new RuntimeException("No handler found for message")));
    }

    private Mono<Void> handleMessageWith(WsExchange exchange, Object handler) {
        for (HandlerAdapter adapter : this.handlerAdapters) {
            if (adapter.supports(handler)) {
                Mono<HandlerResult> resultMono = adapter.handle(exchange, handler);
                return handleResultMono(exchange, resultMono);
            }
        }
        return Mono.error(new RuntimeException("No handler adapter found for " + handler.getClass().getName()));
    }

    private Mono<Void> handleResultMono(WsExchange exchange, Mono<HandlerResult> resultMono) {
        for (HandlerAdapter adapter : this.handlerAdapters) {
            if (adapter instanceof DispatchExceptionHandler exceptionHandler) {
                resultMono = resultMono.onErrorResume(e -> exceptionHandler.handleException(exchange, e));
            }
        }
        return resultMono.flatMap(result -> {
            Mono<Void> voidMono = handleResult(exchange, result, "Handler" + result.getHandler());
            if (result.getExceptionHandler() != null) {
                voidMono = voidMono.onErrorResume(e -> 
                        result.getExceptionHandler().handleException(exchange, e).flatMap(r ->
                                handleResult(exchange, r, "ExceptionHandler" + r.getHandler() + ", error=\"" + e.getMessage() + "\"")));
            }
            return voidMono;
        });
    }

    private Mono<Void> handleResult(WsExchange exchange, HandlerResult result, String desc) {
        for (HandlerResultHandler handler : this.resultHandlers) {
            if (handler.supports(result)) {
                desc += "[DispatcherHandler]";
                return handler.handleResult(exchange, result).checkpoint(desc);
            }
        }
        return Mono.error(new RuntimeException("No result handler found for " + result.getReturnValue()));
    }
}
