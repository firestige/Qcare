package xyz.firestige.qcare.server.core.agent.ws;

public record WsMessage(
        String id,
        String action,
        String msgType,
        String payload,
        String transactionId
) {
}
