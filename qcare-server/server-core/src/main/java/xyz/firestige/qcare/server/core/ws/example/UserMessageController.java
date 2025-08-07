package xyz.firestige.qcare.server.core.ws.example;

import org.springframework.web.reactive.socket.WebSocketSession;

import xyz.firestige.qcare.server.core.ws.server.Message;
import xyz.firestige.qcare.server.core.ws.server.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.server.annotation.WsMsgController;

/**
 * 消息控制器示例
 */
@WsMsgController
public class UserMessageController {

    @RouteMapping(route = "user.login", type = "request")
    public Message<LoginResponse> handleLogin(Message<LoginRequest> message, WebSocketSession session) {
        LoginRequest request = message.getPayload();
        
        // 模拟登录处理
        LoginResponse response = new LoginResponse();
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            response.setSuccess(true);
            response.setToken("sample-jwt-token");
            response.setMessage("登录成功");
        } else {
            response.setSuccess(false);
            response.setMessage("用户名或密码错误");
        }
        
        return new Message<>(message.getId(), "response", message.getRoute(), response);
    }

    @RouteMapping(route = "user.info", type = "request")
    public Message<UserInfo> getUserInfo(Message<GetUserInfoRequest> message) {
        GetUserInfoRequest request = message.getPayload();
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(request.getUserId());
        userInfo.setUsername("admin");
        userInfo.setEmail("admin@example.com");
        
        return new Message<>(message.getId(), "response", message.getRoute(), userInfo);
    }

    // 请求响应模型
    public static class LoginRequest {
        private String username;
        private String password;

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

    public static class GetUserInfoRequest {
        private String userId;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    public static class UserInfo {
        private String userId;
        private String username;
        private String email;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
