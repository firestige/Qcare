package xyz.firestige.qcare.server.core.ws.link;

import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

/**
 * WebSocket处理器包装类
 */
public class AnnotationWebSocketHandler implements WebSocketHandler {
    
    private final WebSocketLifeCircleAware handler;
    private final WebSocketSessionManager sessionManager;

    public AnnotationWebSocketHandler(WebSocketLifeCircleAware handler, WebSocketSessionManager sessionManager) {
        this.handler = handler;
        this.sessionManager = sessionManager;
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull WebSocketSession session) {
        // 获取连接URL，用于会话管理器注册
        String url = session.getHandshakeInfo().getUri().toString();
        
        // 注册会话到管理器
        sessionManager.registerSession(url, session);
        
        // 连接建立时调用 onOpen
        return handler.onOpen(session)
                .thenMany(session.receive())
                .flatMap(msg -> handler.onMessage(session, msg))
                .onErrorContinue((cause, unused) -> handler.onError(cause, session))
                .doFinally(signalType -> doFinally(session))
                .then();
    }

    private void doFinally(WebSocketSession session) {
        // 连接关闭时调用 onClose
        handler.onClose(session);

        // 从管理器中注销会话
        sessionManager.unregisterSession(session.getHandshakeInfo().getUri().toString());
    }
}
