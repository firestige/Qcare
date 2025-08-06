# WebSocket 框架使用指南

## 概述

这是一个基于 Spring WebFlux 的 WebSocket 框架，提供了类似 SpringMVC 的注解体系，支持强类型消息处理和三种消息发送模式。

## 核心组件

### 1. WebSocketSessionManager
负责管理 WebSocket 会话，提供三种消息发送模式：
- **推送模式**: `Mono<Void> send(msg)` - 仅发送不关心响应
- **请求-响应模式**: `<RESP> Mono<RESP> send(req)` - 需要返回请求对应的响应
- **事件流模式**: `<Event> Flux<Event> send(msg)` - 一条消息多条返回，用于订阅场景

### 2. WsClient
WebSocket 客户端统一入口，通过 `WsClient(url)` 创建实例，封装了 WebSocketSessionManager 的功能。

### 3. 注解体系
- `@Websocket`: 标记 WebSocket 处理器类
- `@OnOpen`, `@OnMessage`, `@OnError`, `@OnClose`: 生命周期注解
- `@WsMsgController`: 消息控制器注解
- `@MsgMapping`: 消息路由映射注解

## 使用示例

### 服务端处理器

```java
@Websocket("/ws/echo")
public class EchoWebSocketHandler {
    @OnOpen
    public void onOpen(WebSocketSession session) {
        System.out.println("连接建立: " + session.getId());
    }

    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage message) {
        // 处理消息
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        System.out.println("连接关闭: " + session.getId());
    }
}
```

### 消息控制器

```java
@WsMsgController
public class UserController {
    @MsgMapping(route = "user.login", type = "request")
    public Message<LoginResponse> login(Message<LoginRequest> message) {
        // 处理登录逻辑
        LoginResponse response = new LoginResponse();
        // ...
        return new Message<>(message.getId(), "response", message.getRoute(), response);
    }
}
```

### 客户端使用

```java
@Service
public class ClientService {
    @Autowired
    private WsClientFactory clientFactory;

    public void useWebSocket() {
        // 创建客户端
        WsClient client = clientFactory.createClient("ws://localhost:8080/ws/message");
        
        // 连接
        client.connect().subscribe();
        
        // 1. 推送模式
        Message<String> notification = WsClient.createMessage("notification", "Hello!");
        client.send(notification).subscribe();
        
        // 2. 请求-响应模式
        Message<LoginRequest> request = WsClient.createRequest("user.login", new LoginRequest());
        client.sendRequest(request, LoginResponse.class)
                .doOnNext(response -> {
                    // 处理响应
                })
                .subscribe();
        
        // 3. 事件流模式
        Message<SubscribeRequest> subscribe = WsClient.createSubscription("events.subscribe", new SubscribeRequest());
        client.sendAndSubscribe(subscribe, EventData.class)
                .take(10) // 接收10个事件
                .doOnNext(event -> {
                    // 处理事件
                })
                .subscribe();
    }
}
```

## 消息结构

```java
public class Message<T> {
    private String id;        // 消息ID，用于请求-响应关联
    private String type;      // 消息类型：request/response/event/subscribe
    private String route;     // 路由路径
    private String action;    // 动作（可选）
    private T payload;        // 消息载荷
}
```

## 配置

框架会自动扫描并注册所有标记了相关注解的类，无需额外配置。确保在 Spring Boot 应用中包含以下组件扫描：

```java
@SpringBootApplication
@ComponentScan(basePackages = "xyz.firestige.qcare.server.core.ws")
public class Application {
    // ...
}
```

## 测试

启动应用后，可以通过以下URL测试：
- `ws://localhost:8080/ws/echo` - Echo处理器
- `ws://localhost:8080/ws/message` - 消息路由处理器

使用提供的 `websocket-test.html` 页面进行交互式测试。

## 特性

- ✅ 类似 SpringMVC 的注解体系
- ✅ 强类型消息支持
- ✅ 自动参数注入
- ✅ 路由模式匹配（支持通配符）
- ✅ 三种消息发送模式
- ✅ 会话管理
- ✅ 响应式编程支持
- ✅ 错误处理
- ✅ 自动配置
