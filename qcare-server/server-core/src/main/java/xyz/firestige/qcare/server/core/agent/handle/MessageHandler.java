package xyz.firestige.qcare.server.core.agent.handle;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.core.agent.ws.WsMessage;

public interface MessageHandler {
    Mono<Void> handleMessage(WsMessage message);
}
