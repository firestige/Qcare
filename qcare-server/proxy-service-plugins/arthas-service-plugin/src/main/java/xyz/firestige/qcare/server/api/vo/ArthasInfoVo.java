package xyz.firestige.qcare.server.api.vo;

import xyz.firestige.qcare.server.service.arthas.ConnectionState;

public record ArthasInfoVo(
        String ip,
        int port,
        int pid,
        ConnectionState state
) {
}
