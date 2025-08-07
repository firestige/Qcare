package xyz.firestige.qcare.server.core.agent.ws;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.server.common.ws.message.*;
import xyz.firestige.qcare.server.core.ws.server.annotation.RouteMapping;
import xyz.firestige.qcare.server.core.ws.server.annotation.WsMsgController;

@WsMsgController
public class RegisterController{
    @RouteMapping(value = "agent.register")
    public Mono<AckMessage> register(RegisterMessage message) {
        // Handle the registration logic here
        // For example, you might want to save the registration details to a database or perform some validation
        // Return an acknowledgment message upon successful registration
        return Mono.just(new AckMessage("Registration successful for user: " + message.getUserId()));
    }

    @RouteMapping(value = "agent.unregister")
    public Mono<AckMessage> unregister(UnregisterMessage message) {
        // Handle the unregistration logic here
        // For example, you might want to remove the registration details from a database or perform some cleanup
        // Return an acknowledgment message upon successful unregistration
        return Mono.just(new AckMessage("Unregistration successful for user: " + message.getUserId()));
    }

    @RouteMapping(value = "agent.heartbeat")
    public Mono<PongMessage> heartbeat(PingMessage message) {
        // Handle the heartbeat logic here
        // This could involve updating the last seen timestamp for the user or instance
        // Return a pong message to acknowledge the heartbeat
        return Mono.just(new PongMessage("Heartbeat received from user: " + message.getUserId()));
    }
}
