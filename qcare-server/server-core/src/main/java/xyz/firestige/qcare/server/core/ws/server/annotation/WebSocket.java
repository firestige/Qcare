package xyz.firestige.qcare.server.core.ws.server.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface WebSocket {
    @AliasFor(annotation = Component.class)
    String value() default "";

    String path() default ""; // The path for the WebSocket endpoint, e.g., "/ws/chat"
}
