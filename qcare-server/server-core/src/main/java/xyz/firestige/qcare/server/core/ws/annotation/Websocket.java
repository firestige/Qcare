package xyz.firestige.qcare.server.core.ws.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * WebSocket处理器注解，类似于@Controller
 * 用于标记WebSocket处理器类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Websocket {
    /**
     * WebSocket路径，用于URL映射
     */
    String[] value() default {};
    
    /**
     * WebSocket路径，别名
     */
    String[] path() default {};
}
