package xyz.firestige.qcare.server.api.vo;

public record ArthasAgentInfoVo(
        String agentId,
        String agentVersion,
        InstanceInfo instanceInfo,
        List<ArthasInfoVo> arthasInfoList
) {
}
