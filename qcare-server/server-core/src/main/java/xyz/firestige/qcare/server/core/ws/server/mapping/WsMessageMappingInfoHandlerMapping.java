package xyz.firestige.qcare.server.core.ws.server.mapping;

import xyz.firestige.qcare.server.core.ws.server.WsExchange;
import xyz.firestige.qcare.server.core.ws.server.method.HandlerMethod;
import xyz.firestige.qcare.server.core.ws.server.method.WsMessageInfo;

import java.util.Comparator;
import java.util.Set;

public abstract class WsMessageMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<WsMessageInfo>{
    @Override
    protected Set<String> getDirectRoutes(WsMessageInfo info) {
        return info.getDirectRoutes();
    }

    @Override
    protected WsMessageInfo getMatchingMapping(WsMessageInfo info, WsExchange exchange) {
        return info.getMatchingCondition(exchange);
    }

    @Override
    protected Comparator<WsMessageInfo> getMappingComparator(WsExchange exchange) {
        return (info1, info2) -> info1.compareTo(info2, exchange);
    }

    @Override
    protected void handleMatch(WsMessageInfo match, HandlerMethod handlerMethod, WsExchange exchange) {
        super.handleMatch(match, handlerMethod, exchange);
        // 为exchange添加属性
    }
}
