package xyz.firestige.qcare.server.core.ws.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WebSocket框架演示应用
 * 
 * 启动后可以通过以下URL测试：
 * - ws://localhost:8080/ws/echo - Echo处理器
 * - ws://localhost:8080/ws/message - 消息路由处理器
 */
@SpringBootApplication
@ComponentScan(basePackages = "xyz.firestige.qcare.server.core.ws")
public class WebSocketDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketDemoApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
