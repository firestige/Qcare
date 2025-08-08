package xyz.firestige.qcare.server.common.ws.message;

import java.time.Instant;

public record RegisterMessage(
        String agentId,
        String ip,
        String instanceId,
        String serviceName,
        Instant timestamp
) {
    @Override
    public String toString() {
        return "RegisterMessage{" +
                "agentId='" + agentId + '\'' +
                ", ip='" + ip + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
