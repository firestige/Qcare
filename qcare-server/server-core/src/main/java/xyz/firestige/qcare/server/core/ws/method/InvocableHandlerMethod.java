package xyz.firestige.qcare.server.core.ws.method;

import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.ws.HandlerResult;
import xyz.firestige.qcare.server.core.ws.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvocableHandlerMethod extends HandlerMethod {

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod.getBean(), handlerMethod.getMethod());
    }

    /**
     * 调用处理器方法
     */
    public Mono<HandlerResult> invoke(WebSocketSession session, Message<?> message) {
        return Mono.fromCallable(() -> {
            try {
                // 准备方法参数
                Object[] args = resolveArguments(session, message);

                // 调用方法
                Object result = doInvoke(args);

                // 包装结果
                return new HandlerResult(this, result, message);

            } catch (Exception ex) {
                throw new RuntimeException("Failed to invoke handler method", ex);
            }
        });
    }

    /**
     * 解析方法参数
     */
    private Object[] resolveArguments(WebSocketSession session, Message<?> message) {
        int paramCount = getMethodParameters().length;
        Object[] args = new Object[paramCount];

        for (int i = 0; i < paramCount; i++) {
            Class<?> paramType = getMethodParameters()[i].getParameterType();

            // 根据参数类型注入相应的对象
            if (WebSocketSession.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else if (Message.class.isAssignableFrom(paramType)) {
                args[i] = message;
            } else if (paramType.equals(message.getPayload().getClass())) {
                args[i] = message.getPayload();
            } else {
                // 默认为null，可以扩展更多参数解析逻辑
                args[i] = null;
            }
        }

        return args;
    }

    /**
     * 执行方法调用
     */
    private Object doInvoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
        Method method = getMethod();
        ReflectionUtils.makeAccessible(method);
        return method.invoke(getBean(), args);
    }
}
