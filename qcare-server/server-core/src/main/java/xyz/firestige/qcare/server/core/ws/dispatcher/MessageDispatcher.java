package xyz.firestige.qcare.server.core.ws.dispatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import xyz.firestige.qcare.server.core.ws.Message;
import xyz.firestige.qcare.server.core.ws.mapping.HandlerMapping;

/**
 * 消息分发器，类似于DispatcherHandler
 */
public class MessageDispatcher {
    
    private final List<HandlerMapping> handlerMappings;
    private final ObjectMapper objectMapper;

    public MessageDispatcher(List<HandlerMapping> handlerMappings, ObjectMapper objectMapper) {
        this.handlerMappings = handlerMappings;
        this.objectMapper = objectMapper;
    }

    public Message<?> handleMessage(String messageJson, WebSocketSession session) throws Exception {
        // 先解析为原始Message
        Message<?> rawMessage = objectMapper.readValue(messageJson, Message.class);
        
        // 查找匹配的处理器
        HandlerMapping mapping = findHandler(rawMessage);
        if (mapping == null) {
            throw new IllegalArgumentException("No handler found for message: " + rawMessage.getRoute());
        }
        
        // 调用处理器方法
        return invokeHandler(mapping, messageJson, rawMessage, session);
    }

    private HandlerMapping findHandler(Message<?> message) {
        return handlerMappings.stream()
                .filter(mapping -> mapping.matches(message))
                .findFirst()
                .orElse(null);
    }

    private Message<?> invokeHandler(HandlerMapping mapping, String messageJson, 
                                   Message<?> rawMessage, WebSocketSession session) throws Exception {
        Method method = mapping.getMethod();
        Object handler = mapping.getHandler();
        
        // 准备方法参数
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Class<?> paramType = param.getType();
            
            if (Message.class.isAssignableFrom(paramType)) {
                // 强类型反序列化Message
                args[i] = deserializeMessage(messageJson, param);
            } else if (WebSocketSession.class.isAssignableFrom(paramType)) {
                args[i] = session;
            } else if (String.class.equals(paramType)) {
                args[i] = messageJson;
            } else {
                // 尝试从payload反序列化
                if (rawMessage.getPayload() != null) {
                    args[i] = objectMapper.convertValue(rawMessage.getPayload(), paramType);
                }
            }
        }
        
        // 调用方法
        Object result = method.invoke(handler, args);
        
        // 处理返回值
        if (result instanceof Message) {
            return (Message<?>) result;
        } else if (result != null) {
            // 包装成响应消息
            Message<Object> response = new Message<>();
            response.setId(rawMessage.getId());
            response.setType("response");
            response.setRoute(rawMessage.getRoute());
            response.setPayload(result);
            return response;
        }
        
        return null;
    }

    private Message<?> deserializeMessage(String messageJson, Parameter param) throws Exception {
        // 获取泛型类型信息
        java.lang.reflect.Type parameterizedType = param.getParameterizedType();
        if (parameterizedType instanceof java.lang.reflect.ParameterizedType pt) {
            java.lang.reflect.Type[] actualTypes = pt.getActualTypeArguments();
            if (actualTypes.length > 0) {
                Class<?> payloadType = (Class<?>) actualTypes[0];
                return objectMapper.readValue(messageJson, 
                    objectMapper.getTypeFactory().constructParametricType(Message.class, payloadType));
            }
        }
        
        // 默认返回原始Message
        return objectMapper.readValue(messageJson, Message.class);
    }
}
