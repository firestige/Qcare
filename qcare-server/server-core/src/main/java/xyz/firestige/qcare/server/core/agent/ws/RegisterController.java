package xyz.firestige.qcare.server.core.agent.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.common.ws.message.*;
import xyz.firestige.qcare.server.core.ws.server.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.server.annotation.WsMsgController;
import xyz.firestige.qcare.server.core.ws.server.method.annotation.MessagePayload;

@WsMsgController
public class RegisterController{
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @RouteMapping(value = "/agent/register")
    public Mono<AckMessage> register(@MessagePayload RegisterMessage message) {
        return Mono.just(message)
                .doOnNext(msg -> logger.info("receive: {}", msg.toString()))
                .then(Mono.just(new AckMessage(message.instanceId(), 200, "ok")));
    }

    @RouteMapping(value = "/agent/unregister")
    public Mono<AckMessage> unregister(UnregisterMessage message) {
        // Handle the unregistration logic here
        // For example, you might want to remove the registration details from a database or perform some cleanup
        // Return an acknowledgment message upon successful unregistration
        return Mono.empty();
    }

    @RouteMapping(value = "/agent/heartbeat")
    public Mono<PongMessage> heartbeat(PingMessage message) {
        // Handle the heartbeat logic here
        // This could involve updating the last seen timestamp for the user or instance
        // Return a pong message to acknowledge the heartbeat
        return Mono.empty();
    }
}
