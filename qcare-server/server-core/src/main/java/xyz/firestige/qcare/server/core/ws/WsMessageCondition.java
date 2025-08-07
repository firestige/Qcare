package xyz.firestige.qcare.server.core.ws;

public interface WsMessageCondition<T> {
    T combine(T other);
    T getMatchingCondition(WsExchange exchange);
    int compareTo(T other, WsExchange exchange);
}
