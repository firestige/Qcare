package xyz.firestige.qcare.server.core.ws.server.method.annotation;

import xyz.firestige.qcare.server.core.ws.server.WsExchange;

public abstract class AbstractMessageReaderArgumentResolver extends HandlerMethodArgumentResolverSupport {

    protected abstract Object extraFromWsMessage(WsExchange exchange, Class<?> klass);
}
