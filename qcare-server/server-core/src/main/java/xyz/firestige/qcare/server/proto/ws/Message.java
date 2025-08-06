package xyz.firestige.qcare.server.proto.ws;

public record Message(
        String id,
        String action,
        MsgType msgType,
        String payload,
        String transactionId
) {

}
