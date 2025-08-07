package xyz.firestige.qcare.server.core.ws.method;

import xyz.firestige.qcare.server.core.ws.WsExchange;
import xyz.firestige.qcare.server.core.ws.WsMessageCondition;

import java.util.Set;

public final class WsMessageInfo implements WsMessageCondition<WsMessageInfo> {
    @Override
    public WsMessageInfo combine(WsMessageInfo other) {
        return null;
    }

    @Override
    public WsMessageInfo getMatchingCondition(WsExchange exchange) {
        return null;
    }

    @Override
    public int compareTo(WsMessageInfo other, WsExchange exchange) {
        return 0;
    }

    public Set<String> getDirectPaths() {
    }
}
