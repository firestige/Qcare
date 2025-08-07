package xyz.firestige.qcare.server.core.ws.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * WebSocket消息控制器注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface WsMsgController {
    /**
     * 控制器名称
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * 握手路径
     */
    String path() default "";
}
