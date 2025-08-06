package xyz.firestige.qcare.server.core.ws.annotation;

import org.springframework.core.annotation.AliasFor;

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
public @interface RouteMapping {
    /**
     * 路由路径，支持模式匹配
     */
    @AliasFor("route")
    String[] value() default {};
    
    /**
     * 路由路径，别名
     */
    @AliasFor("value")
    String[] route() default {};
}
