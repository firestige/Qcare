package xyz.firestige.qcare.server.api.vo;

import java.util.List;

public record ArthasAgentInfoVo(
        String agentId,
        String agentVersion,
        InstanceInfo instanceInfo,
        List<ArthasInfoVo> arthasInfoList
) {
}
