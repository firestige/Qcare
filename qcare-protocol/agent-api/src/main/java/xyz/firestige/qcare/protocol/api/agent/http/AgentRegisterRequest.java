package xyz.firestige.qcare.protocol.api.agent.http;

public record AgentRegisterRequest(
    String id,
    String name,
    String type,
    String version
) {
}
