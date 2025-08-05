package xyz.firestige.qcare.server.core.infra.agent.ws;

import java.util.Objects;

public record GenericMessage(
        String type,
        Objects payload
) {
    public boolean isHeartbeat() {
        return "heartbeat".equals(type);
    }
}
