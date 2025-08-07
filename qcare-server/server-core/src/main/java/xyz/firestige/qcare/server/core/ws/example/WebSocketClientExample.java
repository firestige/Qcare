package xyz.firestige.qcare.server.core.ws.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.server.Message;
import xyz.firestige.qcare.server.core.ws.client.WsClient;
import xyz.firestige.qcare.server.core.ws.client.WsClientFactory;

/**
 * WebSocket客户端使用示例
 */
@Service
public class WebSocketClientExample {

    private final WsClientFactory clientFactory;

    @Autowired
    public WebSocketClientExample(WsClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * 演示三种消息发送模式
     */
    public void demonstrateMessagePatterns() {
        // 创建客户端
        WsClient client = clientFactory.createClient("ws://localhost:8080/ws/message");

        // 连接
        client.connect()
                .doOnSuccess(v -> System.out.println("Connected to WebSocket"))
                .doOnError(e -> System.err.println("Connection failed: " + e.getMessage()))
                .subscribe();

        // 1. 推送模式：仅发送不关心响应
        pushExample(client);

        // 2. 请求-响应模式：发送请求并等待响应
        requestResponseExample(client);

        // 3. 事件流模式：发送订阅请求并接收多个事件
        eventStreamExample(client);
    }

    /**
     * 推送模式示例
     */
    private void pushExample(WsClient client) {
        Message<String> notification = WsClient.createMessage("notification.send", "Hello from client!");
        
        client.send(notification)
                .doOnSuccess(v -> System.out.println("Notification sent successfully"))
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }

    /**
     * 请求-响应模式示例
     */
    private void requestResponseExample(WsClient client) {
        // 创建登录请求
        LoginRequest loginRequest = new LoginRequest("admin", "password");
        Message<LoginRequest> request = WsClient.createRequest("user.login", loginRequest);

        client.sendRequest(request, LoginResponse.class)
                .doOnNext(response -> {
                    System.out.println("Login response received:");
                    System.out.println("Success: " + response.isSuccess());
                    System.out.println("Token: " + response.getToken());
                    System.out.println("Message: " + response.getMessage());
                })
                .doOnError(e -> System.err.println("Login request failed: " + e.getMessage()))
                .subscribe();
    }

    /**
     * 事件流模式示例
     */
    private void eventStreamExample(WsClient client) {
        // 创建订阅请求
        SubscribeRequest subscribeRequest = new SubscribeRequest("user.activity");
        Message<SubscribeRequest> request = WsClient.createSubscription("events.subscribe", subscribeRequest);

        client.sendAndSubscribe(request, ActivityEvent.class)
                .take(10) // 只接收前10个事件
                .doOnNext(event -> {
                    System.out.println("Activity event received:");
                    System.out.println("User: " + event.getUserId());
                    System.out.println("Activity: " + event.getActivity());
                    System.out.println("Timestamp: " + event.getTimestamp());
                })
                .doOnComplete(() -> System.out.println("Event stream completed"))
                .doOnError(e -> System.err.println("Event stream error: " + e.getMessage()))
                .subscribe();
    }

    /**
     * 断开连接示例
     */
    public void disconnectExample() {
        WsClient client = clientFactory.createClient("ws://localhost:8080/ws/message");
        
        client.connect()
                .then(Mono.delay(java.time.Duration.ofSeconds(5))) // 连接5秒后断开
                .then(client.disconnect())
                .doOnSuccess(v -> System.out.println("Disconnected from WebSocket"))
                .subscribe();
    }

    // 内部类定义
    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private boolean success;
        private String token;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class SubscribeRequest {
        private String topic;

        public SubscribeRequest() {}

        public SubscribeRequest(String topic) {
            this.topic = topic;
        }

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
    }

    public static class ActivityEvent {
        private String userId;
        private String activity;
        private long timestamp;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
