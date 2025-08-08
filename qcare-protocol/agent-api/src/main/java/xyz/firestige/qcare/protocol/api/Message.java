package xyz.firestige.qcare.protocol.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * WebSocket消息结构
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message<T> {
    private String id;
    private String type;
    private String route;
    private String action;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private T payload;

    public Message() {}

    public Message(String id, String type, String route, String action, T payload) {
        this.id = id;
        this.type = type;
        this.route = route;
        this.payload = payload;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Message<?> createResponse(Object payload) {
        Message<?> resp = new Message<>(this.id, this.type, route, this.action, payload);
        resp.type = "RESPONSE";
        return resp;
    }
}
