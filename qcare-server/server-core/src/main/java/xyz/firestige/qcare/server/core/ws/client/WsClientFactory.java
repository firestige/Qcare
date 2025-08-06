package xyz.firestige.qcare.server.core.ws.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.firestige.qcare.server.core.ws.manager.WebSocketSessionManager;

/**
 * WebSocket客户端工厂
 */
@Component
public class WsClientFactory {

    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WsClientFactory(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * 创建WebSocket客户端
     */
    public WsClient createClient(String url) {
        return new WsClient(url, sessionManager);
    }
}
