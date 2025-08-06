package xyz.firestige.qcare.server.common.ws.message;

import java.time.Instant;

public record RegisterMessage(
        String agentId,
        String ip,
        String instanceId,
        String serviceName,
        Instant timestamp
) {
}
