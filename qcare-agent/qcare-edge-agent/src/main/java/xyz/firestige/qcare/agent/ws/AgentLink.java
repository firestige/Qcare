package xyz.firestige.qcare.agent.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class AgentLink {
    private static final Logger log = LoggerFactory.getLogger(AgentLink.class);
    private final AtomicReference<AgentClientWebSocket> clientRef = new AtomicReference<>();
    public AgentLink() {}
    void setLink(AgentClientWebSocket newClient) {
        AgentClientWebSocket oldClient = clientRef.getAndSet(newClient);
        if (oldClient != null) {
            try {
                if (oldClient.isConnected()) {
                    oldClient.close();
                }
            } catch (Exception e) {
                log.warn("ws client close error.", e);
            }
        }
    }

    public boolean isConnected() {
        return clientRef.get() != null && clientRef.get().isConnected();
    }

    public void sendMessage(String message) {
        clientRef.get().send(message);
    }

    public Mono<String> sendMessageAsync(String message) {
        return clientRef.get().sendAsync(message);
    }
}
