package xyz.firestige.qcare.server.core.ws.link;

import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

/**
 * WebSocket处理器包装类
 */
public class AnnotationWebSocketHandler implements WebSocketHandler {
    
    private final WebSocketLifeCircleAware delegator;

    public AnnotationWebSocketHandler(WebSocketLifeCircleAware handler) {
        this.delegator = handler;
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull final WebSocketSession session) {
        // 连接建立时调用 onOpen
        return delegator.onOpen(session)
                .thenMany(session.receive())
                .flatMap(msg -> delegator.onMessage(session, msg))
                .onErrorContinue((cause, unused) -> delegator.onError(cause, session))
                .doFinally(signalType -> doFinally(session))
                .then();
    }

    private void doFinally(WebSocketSession session) {
        // 连接关闭时调用 onClose
        delegator.onClose(session);
    }
}
