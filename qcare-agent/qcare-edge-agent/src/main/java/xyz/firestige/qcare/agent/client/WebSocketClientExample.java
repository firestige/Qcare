package xyz.firestige.qcare.agent.client;

import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 客户端示例
 * 展示如何使用 Micronaut WebSocket 客户端连接到服务器
 */
@ClientWebSocket("/ws/agent")
public class WebSocketClientExample {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientExample.class);

    private WebSocketSession session;
    private final ConcurrentHashMap<String, Object> messageHandlers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isConnected = false;

    @OnOpen
    public void onOpen(WebSocketSession session) {
        this.session = session;
        this.isConnected = true;
        logger.info("WebSocket 连接已建立: {}", session.getId());

        // 开始心跳
        startHeartbeat();

        // 发送初始化消息
        sendInitMessage();
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("收到 WebSocket 消息: {}", message);

        try {
            // 这里可以解析消息并处理不同类型的命令
            handleMessage(message);
        } catch (Exception e) {
            logger.error("处理 WebSocket 消息失败", e);
        }
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        this.isConnected = false;
        logger.info("WebSocket 连接已关闭: {}", session.getId());

        // 停止心跳
        scheduler.shutdown();

        // 可以在这里实现重连逻辑
        scheduleReconnect();
    }

    /**
     * 发送消息到服务器
     */
    public void sendMessage(String message) {
        if (session != null && isConnected) {
            session.sendAsync(message).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("发送 WebSocket 消息失败", throwable);
                } else {
                    logger.debug("WebSocket 消息发送成功: {}", message);
                }
            });
        } else {
            logger.warn("WebSocket 未连接，无法发送消息: {}", message);
        }
    }

    /**
     * 发送初始化消息
     */
    private void sendInitMessage() {
        String initMessage = String.format(
            "{\"type\":\"init\",\"agentId\":\"%s\",\"timestamp\":%d}",
            getAgentId(),
            System.currentTimeMillis()
        );
        sendMessage(initMessage);
    }

    /**
     * 开始心跳
     */
    private void startHeartbeat() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isConnected) {
                String heartbeat = String.format(
                    "{\"type\":\"heartbeat\",\"agentId\":\"%s\",\"timestamp\":%d}",
                    getAgentId(),
                    System.currentTimeMillis()
                );
                sendMessage(heartbeat);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 处理收到的消息
     */
    private void handleMessage(String message) {
        // 简单的消息处理示例
        if (message.contains("\"type\":\"command\"")) {
            handleCommandMessage(message);
        } else if (message.contains("\"type\":\"heartbeat_ack\"")) {
            logger.debug("收到心跳确认");
        } else if (message.contains("\"type\":\"config\"")) {
            handleConfigMessage(message);
        } else {
            logger.info("收到未知类型消息: {}", message);
        }
    }

    /**
     * 处理命令消息
     */
    private void handleCommandMessage(String message) {
        logger.info("处理命令消息: {}", message);

        // 这里可以解析具体的命令并执行
        // 例如：执行系统命令、收集监控数据等

        // 发送命令执行结果
        String response = String.format(
            "{\"type\":\"command_result\",\"agentId\":\"%s\",\"result\":\"success\",\"timestamp\":%d}",
            getAgentId(),
            System.currentTimeMillis()
        );
        sendMessage(response);
    }

    /**
     * 处理配置消息
     */
    private void handleConfigMessage(String message) {
        logger.info("处理配置消息: {}", message);

        // 这里可以更新代理配置

        // 发送配置更新确认
        String response = String.format(
            "{\"type\":\"config_ack\",\"agentId\":\"%s\",\"timestamp\":%d}",
            getAgentId(),
            System.currentTimeMillis()
        );
        sendMessage(response);
    }

    /**
     * 计划重连
     */
    private void scheduleReconnect() {
        scheduler.schedule(() -> {
            logger.info("尝试重新连接 WebSocket");
            // 这里可以实现重连逻辑
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 获取代理 ID
     */
    private String getAgentId() {
        // 这里可以从配置或环境变量中获取
        return System.getProperty("agent.id", "edge-agent-" + System.currentTimeMillis());
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return isConnected && session != null;
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (session != null) {
            session.close();
        }
        scheduler.shutdown();
    }
}
