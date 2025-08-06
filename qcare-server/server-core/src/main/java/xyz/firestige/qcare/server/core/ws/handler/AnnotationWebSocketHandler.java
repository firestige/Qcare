package xyz.firestige.qcare.server.core.ws.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.manager.WebSocketSessionManager;

/**
 * WebSocket处理器包装类
 */
public class AnnotationWebSocketHandler implements WebSocketHandler {
    
    private final Object handler;
    private final Method onOpenMethod;
    private final Method onMessageMethod;
    private final Method onErrorMethod;
    private final Method onCloseMethod;
    private final WebSocketSessionManager sessionManager;

    public AnnotationWebSocketHandler(Object handler, Method onOpenMethod, 
                                    Method onMessageMethod, Method onErrorMethod, Method onCloseMethod,
                                    WebSocketSessionManager sessionManager) {
        this.handler = handler;
        this.onOpenMethod = onOpenMethod;
        this.onMessageMethod = onMessageMethod;
        this.onErrorMethod = onErrorMethod;
        this.onCloseMethod = onCloseMethod;
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
        if (onOpenMethod != null) {
            try {
                invokeMethod(onOpenMethod, session, null, null);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }

        // 处理消息
        return session.receive()
                .doOnNext(message -> {
                    if (onMessageMethod != null) {
                        try {
                            invokeMethod(onMessageMethod, session, message, null);
                        } catch (Exception e) {
                            // 错误处理
                            if (onErrorMethod != null) {
                                try {
                                    invokeMethod(onErrorMethod, session, null, e);
                                } catch (Exception ex) {
                                    // 忽略错误处理中的异常
                                }
                            }
                        }
                    }
                })
                .doOnError(throwable -> {
                    if (onErrorMethod != null) {
                        try {
                            invokeMethod(onErrorMethod, session, null, throwable);
                        } catch (Exception e) {
                            // 忽略错误处理中的异常
                        }
                    }
                })
                .doFinally(signalType -> {
                    // 连接关闭时注销会话
                    sessionManager.unregisterSession(url);
                    
                    if (onCloseMethod != null) {
                        try {
                            invokeMethod(onCloseMethod, session, null, null);
                        } catch (Exception e) {
                            // 忽略关闭处理中的异常
                        }
                    }
                })
                .then();
    }

    private void invokeMethod(Method method, WebSocketSession session, 
                            WebSocketMessage message, Throwable exception) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> paramType = param.getType();
            
            if (WebSocketSession.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else if (WebSocketMessage.class.isAssignableFrom(paramType)) {
                args[i] = message;
            } else if (Throwable.class.isAssignableFrom(paramType)) {
                args[i] = exception;
            } else if (String.class.equals(paramType) && message != null) {
                args[i] = message.getPayload().toString();
            } else if (URI.class.equals(paramType)) {
                args[i] = session.getHandshakeInfo().getUri();
            } else if (Map.class.isAssignableFrom(paramType)) {
                // 解析查询参数
                args[i] = parseQueryParams(session.getHandshakeInfo().getUri());
            }
        }
        
        method.invoke(handler, args);
    }
    
    private Map<String, String> parseQueryParams(URI uri) {
        // 简单的查询参数解析
        Map<String, String> params = new java.util.HashMap<>();
        if (uri != null && uri.getQuery() != null) {
            String[] pairs = uri.getQuery().split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}
