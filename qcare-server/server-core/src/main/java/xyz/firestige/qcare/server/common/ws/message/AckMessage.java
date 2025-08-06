package xyz.firestige.qcare.server.common.ws.message;

public record AckMessage(
        String referenceId,
        int status,
        String reason
) {
}
