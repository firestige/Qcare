package xyz.firestige.qcare.agent.client;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP 客户端示例
 * 展示如何使用 Micronaut HTTP 客户端发送请求
 */
@Singleton
public class HttpClientExample {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientExample.class);

    @Inject
    @Client("${qcare.server.url:http://localhost:8080}")
    private HttpClient httpClient;

    /**
     * 发送 GET 请求
     */
    public CompletableFuture<String> sendGetRequest(String path) {
        logger.info("发送 GET 请求到: {}", path);

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest<Object> request = HttpRequest.GET(path);
                HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

                logger.info("GET 请求响应状态: {}", response.getStatus());
                return response.body();
            } catch (Exception e) {
                logger.error("GET 请求失败", e);
                throw new RuntimeException("GET 请求失败", e);
            }
        });
    }

    /**
     * 发送 POST 请求
     */
    public CompletableFuture<String> sendPostRequest(String path, Map<String, Object> body) {
        logger.info("发送 POST 请求到: {}", path);

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest<Map<String, Object>> request = HttpRequest.POST(path, body);
                HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

                logger.info("POST 请求响应状态: {}", response.getStatus());
                return response.body();
            } catch (Exception e) {
                logger.error("POST 请求失败", e);
                throw new RuntimeException("POST 请求失败", e);
            }
        });
    }

    /**
     * 注册代理到服务器
     */
    public CompletableFuture<Boolean> registerAgent(String agentId, String agentName, String version) {
        logger.info("注册代理: {} - {}", agentId, agentName);

        Map<String, Object> registrationData = Map.of(
            "agentId", agentId,
            "agentName", agentName,
            "version", version,
            "timestamp", System.currentTimeMillis()
        );

        return sendPostRequest("/api/agent/register", registrationData)
            .thenApply(response -> {
                logger.info("代理注册响应: {}", response);
                return response.contains("success");
            })
            .exceptionally(throwable -> {
                logger.error("代理注册失败", throwable);
                return false;
            });
    }

    /**
     * 发送心跳
     */
    public CompletableFuture<Boolean> sendHeartbeat(String agentId) {
        Map<String, Object> heartbeatData = Map.of(
            "agentId", agentId,
            "timestamp", System.currentTimeMillis(),
            "status", "ONLINE"
        );

        return sendPostRequest("/api/agent/heartbeat", heartbeatData)
            .thenApply(response -> response.contains("ok"))
            .exceptionally(throwable -> {
                logger.error("心跳发送失败", throwable);
                return false;
            });
    }
}
