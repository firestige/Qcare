package xyz.firestige.qcare.server.common.ws.message;

import java.time.Instant;

public record PongMessage(
    Instant timestamp
) {
}
