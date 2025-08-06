package xyz.firestige.qcare.server.core.ws.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息映射注解，类似于@RequestMapping
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsgMapping {
    /**
     * 路由路径，支持模式匹配
     */
    String[] value() default {};
    
    /**
     * 路由路径，别名
     */
    String[] route() default {};
    
    /**
     * 消息类型过滤
     */
    String[] type() default {};
    
    /**
     * 动作过滤
     */
    String[] action() default {};
}
