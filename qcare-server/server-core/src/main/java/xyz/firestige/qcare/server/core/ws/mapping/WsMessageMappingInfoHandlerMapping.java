package xyz.firestige.qcare.server.core.ws.mapping;

import xyz.firestige.qcare.server.core.ws.WsExchange;
import xyz.firestige.qcare.server.core.ws.method.HandlerMethod;
import xyz.firestige.qcare.server.core.ws.method.WsMessageInfo;

import java.util.Comparator;
import java.util.Set;

public abstract class WsMessageMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<WsMessageInfo>{
    @Override
    protected Set<String> getDirectPaths(WsMessageInfo info) {
        return info.getDirectPaths();
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
    }
}
