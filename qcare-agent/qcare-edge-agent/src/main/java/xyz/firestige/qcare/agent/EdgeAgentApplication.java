package xyz.firestige.qcare.agent;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.micronaut.websocket.WebSocketClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.firestige.qcare.agent.client.HttpClientExample;
import xyz.firestige.qcare.agent.client.WebSocketClientExample;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Qcare Edge Agent 主应用程序
 */
@Singleton
public class EdgeAgentApplication {

    private static final Logger logger = LoggerFactory.getLogger(EdgeAgentApplication.class);

    @Inject
    private HttpClientExample httpClient;

    @Inject
    private WebSocketClient webSocketClient;

    private WebSocketClientExample webSocketClientExample;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private volatile boolean running = false;

    public static void main(String[] args) {
        logger.info("启动 Qcare Edge Agent...");

        try (ApplicationContext context = Micronaut.run(EdgeAgentApplication.class, args)) {
            EdgeAgentApplication app = context.getBean(EdgeAgentApplication.class);
            app.start();

            // 保持应用程序运行
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("正在关闭 Qcare Edge Agent...");
                app.stop();
            }));

            // 阻塞主线程
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("启动 Edge Agent 失败", e);
            System.exit(1);
        }
    }

    /**
     * 启动应用程序
     */
    public void start() {
        logger.info("Edge Agent 正在启动...");
        running = true;

        // 初始化代理信息
        String agentId = System.getProperty("agent.id", "edge-agent-" + System.currentTimeMillis());
        String agentName = System.getProperty("agent.name", "QCare Edge Agent");
        String version = System.getProperty("agent.version", "1.0.0");

        // 1. 首先通过 HTTP 注册代理
        registerAgent(agentId, agentName, version)
            .thenCompose(success -> {
                if (success) {
                    logger.info("代理注册成功，开始建立 WebSocket 连接");
                    return connectWebSocket();
                } else {
                    logger.error("代理注册失败");
                    return CompletableFuture.completedFuture(false);
                }
            })
            .thenAccept(connected -> {
                if (connected) {
                    logger.info("Edge Agent 启动完成");
                    startPeriodicTasks(agentId);
                } else {
                    logger.error("WebSocket 连接失败");
                }
            })
            .exceptionally(throwable -> {
                logger.error("启动过程中发生错误", throwable);
                return null;
            });
    }

    /**
     * 注册代理
     */
    private CompletableFuture<Boolean> registerAgent(String agentId, String agentName, String version) {
        return httpClient.registerAgent(agentId, agentName, version);
    }

    /**
     * 连接 WebSocket
     */
    private CompletableFuture<Boolean> connectWebSocket() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String serverUrl = System.getProperty("qcare.server.websocket.url", "ws://localhost:8080/ws/agent");
                URI uri = URI.create(serverUrl);

                // 使用正确的 Micronaut WebSocket 客户端连接方法
                webSocketClientExample = webSocketClient.connect(WebSocketClientExample.class, uri).blockingSingle();

                // 等待连接建立
                Thread.sleep(1000);

                return webSocketClientExample.isConnected();
            } catch (Exception e) {
                logger.error("WebSocket 连接失败", e);
                return false;
            }
        });
    }

    /**
     * 启动周期性任务
     */
    private void startPeriodicTasks(String agentId) {
        // 定期发送心跳 (HTTP)
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                httpClient.sendHeartbeat(agentId)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.warn("HTTP 心跳发送失败", throwable);
                        } else {
                            logger.debug("HTTP 心跳发送成功");
                        }
                    });
            }
        }, 60, 60, TimeUnit.SECONDS);

        // 发送监控数据
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                sendMonitoringData(agentId);
            }
        }, 30, 30, TimeUnit.SECONDS);

        // 检查连接状态
        scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                checkConnectionHealth();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 发送监控数据
     */
    private void sendMonitoringData(String agentId) {
        if (webSocketClientExample != null && webSocketClientExample.isConnected()) {
            // 获取系统监控数据
            Map<String, Object> monitoringData = collectSystemMetrics();

            String message = String.format(
                "{\"type\":\"monitoring\",\"agentId\":\"%s\",\"data\":%s,\"timestamp\":%d}",
                agentId,
                formatMetricsAsJson(monitoringData),
                System.currentTimeMillis()
            );

            webSocketClientExample.sendMessage(message);
            logger.debug("发送监控数据: {}", message);
        }
    }

    /**
     * 收集系统监控数据
     */
    private Map<String, Object> collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return Map.of(
            "cpu", getCpuUsage(),
            "memory", Map.of(
                "used", usedMemory,
                "total", totalMemory,
                "max", maxMemory,
                "usagePercent", (double) usedMemory / maxMemory * 100
            ),
            "disk", getDiskUsage(),
            "threads", Thread.activeCount(),
            "uptime", System.currentTimeMillis() - startTime
        );
    }

    private static final long startTime = System.currentTimeMillis();

    /**
     * 获取 CPU 使用率（简化版本）
     */
    private double getCpuUsage() {
        // 这里使用简化的 CPU 使用率计算
        // 在实际应用中，可以使用更精确的方法
        return Math.random() * 100;
    }

    /**
     * 获取磁盘使用情况
     */
    private Map<String, Object> getDiskUsage() {
        java.io.File root = new java.io.File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        return Map.of(
            "total", totalSpace,
            "used", usedSpace,
            "free", freeSpace,
            "usagePercent", (double) usedSpace / totalSpace * 100
        );
    }

    /**
     * 格式化监控数据为 JSON
     */
    private String formatMetricsAsJson(Map<String, Object> metrics) {
        // 简化的 JSON 序列化
        StringBuilder json = new StringBuilder("{");
        metrics.forEach((key, value) -> {
            json.append("\"").append(key).append("\":");
            if (value instanceof Map) {
                json.append(formatMapAsJson((Map<String, Object>) value));
            } else if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
            json.append(",");
        });
        if (json.length() > 1) {
            json.setLength(json.length() - 1); // 移除最后一个逗号
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 格式化 Map 为 JSON
     */
    private String formatMapAsJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        map.forEach((key, value) -> {
            json.append("\"").append(key).append("\":");
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
            json.append(",");
        });
        if (json.length() > 1) {
            json.setLength(json.length() - 1);
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 检查连接健康状态
     */
    private void checkConnectionHealth() {
        if (webSocketClientExample == null || !webSocketClientExample.isConnected()) {
            logger.warn("WebSocket 连接断开，尝试重连...");
            connectWebSocket().whenComplete((connected, throwable) -> {
                if (connected) {
                    logger.info("WebSocket 重连成功");
                } else {
                    logger.error("WebSocket 重连失败");
                }
            });
        }
    }

    /**
     * 停止应用程序
     */
    public void stop() {
        logger.info("正在停止 Edge Agent...");
        running = false;

        if (webSocketClientExample != null) {
            webSocketClientExample.close();
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        logger.info("Edge Agent 已停止");
    }
}
