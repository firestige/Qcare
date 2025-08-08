package xyz.firestige.qcare.server.core.ws.server.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import xyz.firestige.qcare.protocol.api.Message;

import java.lang.reflect.Type;

@Component
public class MessagePayloadExtractor {

    private final ObjectMapper objectMapper;

    public MessagePayloadExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 从 Message JSON 字符串中提取并反序列化 payload
     *
     * @param messageJson Message 的 JSON 字符串
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 反序列化后的 payload 对象
     */
    public <T> T extractPayload(String messageJson, Class<T> targetType) {
        try {
            // 1. 解析整个 Message
            JsonNode messageNode = objectMapper.readTree(messageJson);

            // 2. 获取 payload 节点
            JsonNode payloadNode = messageNode.get("payload");
            if (payloadNode == null || payloadNode.isNull()) {
                return null;
            }

            // 3. 反序列化 payload 为目标类型
            return objectMapper.treeToValue(payloadNode, targetType);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract payload from message", e);
        }
    }

    /**
     * 从 Message JSON 字符串中提取并反序列化 payload（支持泛型）
     *
     * @param messageJson Message 的 JSON 字符串
     * @param targetType 目标类型（支持泛型）
     * @return 反序列化后的 payload 对象
     */
    public Object extractPayload(String messageJson, Type targetType) {
        try {
            // 1. 解析整个 Message
            JsonNode messageNode = objectMapper.readTree(messageJson);

            // 2. 获取 payload 节点
            JsonNode payloadNode = messageNode.get("payload");
            if (payloadNode == null || payloadNode.isNull()) {
                return null;
            }

            // 3. 构造 JavaType
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            com.fasterxml.jackson.databind.JavaType javaType = typeFactory.constructType(targetType);

            // 4. 反序列化 payload 为目标类型
            return objectMapper.readValue(payloadNode.traverse(), javaType);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract payload from message", e);
        }
    }

    /**
     * 从已解析的 Message 对象中提取并反序列化 payload
     *
     * @param message 已解析的 Message 对象（payload 可能是 JsonNode）
     * @param targetType 目标类型
     * @param <T> 目标类型泛型
     * @return 反序列化后的 payload 对象
     */
    public <T> T extractPayload(Message<?> message, Class<T> targetType) {
        try {
            Object payload = message.getPayload();
            if (payload == null) {
                return null;
            }

            // 如果 payload 已经是目标类型，直接返回
            if (targetType.isInstance(payload)) {
                return targetType.cast(payload);
            }

            // 如果 payload 是 JsonNode，需要转换
            if (payload instanceof JsonNode) {
                return objectMapper.treeToValue((JsonNode) payload, targetType);
            }

            // 其他情况，先转为 JsonNode 再转为目标类型
            JsonNode jsonNode = objectMapper.valueToTree(payload);
            return objectMapper.treeToValue(jsonNode, targetType);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract payload from message object", e);
        }
    }

    /**
     * 从已解析的 Message 对象中提取并反序列化 payload（支持泛型）
     *
     * @param message 已解析的 Message 对象
     * @param targetType 目标类型（支持泛型）
     * @return 反序列化后的 payload 对象
     */
    public Object extractPayload(Message<?> message, Type targetType) {
        try {
            Object payload = message.getPayload();
            if (payload == null) {
                return null;
            }

            // 构造 JavaType
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            com.fasterxml.jackson.databind.JavaType javaType = typeFactory.constructType(targetType);

            // 如果 payload 已经是目标类型，直接返回
            if (javaType.getRawClass().isInstance(payload)) {
                return payload;
            }

            // 如果 payload 是 JsonNode，需要转换
            if (payload instanceof JsonNode) {
                return objectMapper.readValue(((JsonNode) payload).traverse(), javaType);
            }

            // 其他情况，先转为 JsonNode 再转为目标类型
            JsonNode jsonNode = objectMapper.valueToTree(payload);
            return objectMapper.readValue(jsonNode.traverse(), javaType);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract payload from message object", e);
        }
    }

    /**
     * 结合 Spring 的 MethodParameter 使用
     *
     * @param message Message 对象
     * @param parameter 方法参数
     * @return 反序列化后的 payload 对象
     */
    public Object extractPayload(Message<?> message, MethodParameter parameter) {
        Type parameterType = parameter.getGenericParameterType();
        return extractPayload(message, parameterType);
    }
}