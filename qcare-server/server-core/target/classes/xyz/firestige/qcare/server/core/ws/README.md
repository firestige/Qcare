# WebSocket 注解框架使用指南

这个框架提供了类似 Spring MVC 的注解体系来简化 WebSocket 开发，支持基于注解的 WebSocket 处理器和消息路由。

## 核心组件

### 1. 消息结构 (`Message<T>`)

```java
public class Message<T> {
    private String id;      // 消息唯一标识
    private String type;    // 消息类型（request/response/notify等）
    private String route;   // 路由标识
    private String action;  // 动作名称（可选）
    private T payload;      // 强类型载荷
}
```

### 2. WebSocket 处理器注解

#### `@Websocket`
用于标记 WebSocket 处理器类，类似于 `@Controller`：

```java
@Websocket("/ws/echo")
public class EchoWebSocketHandler {
    // ...
}
```

#### 生命周期注解
- `@OnOpen`: 连接建立时调用
- `@OnMessage`: 收到消息时调用
- `@OnError`: 发生错误时调用
- `@OnClose`: 连接关闭时调用

```java
@Websocket("/ws/echo")
public class EchoWebSocketHandler {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        // 连接建立处理
    }
    
    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage message) {
        // 消息处理
    }
    
    @OnError
    public void onError(WebSocketSession session, Throwable error) {
        // 错误处理
    }
    
    @OnClose
    public void onClose(WebSocketSession session) {
        // 连接关闭处理
    }
}
```

### 3. 消息控制器注解

#### `@WsMsgController`
用于标记消息控制器类，类似于 `@RestController`：

```java
@WsMsgController
public class UserMessageController {
    // ...
}
```

#### `@MsgMapping`
用于映射消息路由，类似于 `@RequestMapping`：

```java
@WsMsgController
public class UserMessageController {
    
    @MsgMapping(route = "user.login", type = "request")
    public Message<LoginResponse> handleLogin(Message<LoginRequest> message, WebSocketSession session) {
        // 处理登录请求
        LoginRequest request = message.getPayload();
        // ...
        return new Message<>(message.getId(), "response", message.getRoute(), response);
    }
}
```

## 使用示例

### 1. 创建简单的 WebSocket 处理器

```java
@Websocket("/ws/echo")
public class EchoWebSocketHandler {

    @OnOpen
    public void onOpen(WebSocketSession session) {
        System.out.println("连接建立: " + session.getId());
    }

    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage message) {
        // 回显消息
        session.send(Mono.just(session.textMessage("Echo: " + message.getPayloadAsText()))).subscribe();
    }
}
```

### 2. 创建消息路由控制器

```java
@WsMsgController
public class UserController {

    @MsgMapping(route = "user.login", type = "request")
    public Message<LoginResponse> login(Message<LoginRequest> request) {
        LoginRequest loginData = request.getPayload();
        
        // 处理登录逻辑
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken("jwt-token");
        
        return new Message<>(request.getId(), "response", request.getRoute(), response);
    }
    
    @MsgMapping(route = "user.*", type = "request")  // 支持通配符
    public Message<String> handleUserRequests(Message<?> request) {
        // 处理所有以 user. 开头的请求
        return new Message<>(request.getId(), "response", request.getRoute(), "处理成功");
    }
}
```

### 3. 客户端消息格式

```javascript
// 发送登录请求
const loginMessage = {
    id: "uuid-1",
    type: "request", 
    route: "user.login",
    payload: {
        username: "admin",
        password: "password"
    }
};

websocket.send(JSON.stringify(loginMessage));

// 接收响应
websocket.onmessage = function(event) {
    const response = JSON.parse(event.data);
    if (response.type === "response" && response.route === "user.login") {
        console.log("登录结果:", response.payload);
    }
};
```

## 参数注入支持

方法参数支持自动注入：

- `WebSocketSession`: 当前 WebSocket 会话
- `Message<T>`: 强类型消息对象
- `String`: 原始消息 JSON 字符串
- `URI`: WebSocket 连接 URI
- `Map<String, String>`: URL 查询参数

```java
@MsgMapping("user.profile")
public Message<UserProfile> getProfile(
    Message<GetProfileRequest> request,    // 强类型消息
    WebSocketSession session,              // WebSocket 会话
    Map<String, String> queryParams        // URL 参数
) {
    // 处理逻辑
}
```

## 配置

框架通过 `WebSocketAutoConfiguration` 自动配置，无需手动配置。它会：

1. 扫描所有 `@Websocket` 注解的类并注册为 WebSocket 处理器
2. 扫描所有 `@WsMsgController` 注解的类并注册消息路由
3. 创建 `MessageDispatcher` 用于消息分发

## 高级特性

### 1. 路由通配符支持

```java
@MsgMapping(route = "user.*")        // 匹配 user.login, user.info 等
@MsgMapping(route = "admin.**")      // 匹配 admin.user.create 等多级路由
```

### 2. 类型过滤

```java
@MsgMapping(route = "user.login", type = {"request", "notify"})
public Message<?> handleLogin(Message<?> message) {
    // 只处理 request 和 notify 类型的消息
}
```

### 3. 错误处理

框架会自动捕获处理器中的异常并返回错误响应：

```json
{
    "type": "error",
    "payload": "处理消息时发生错误: ..."
}
```

## 完整示例

参考 `example` 包下的示例代码：

- `EchoWebSocketHandler`: 简单回显处理器
- `UserMessageController`: 用户消息控制器
- `MessageRoutingWebSocketHandler`: 消息路由处理器
