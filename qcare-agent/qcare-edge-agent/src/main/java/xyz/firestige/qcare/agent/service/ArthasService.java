package xyz.firestige.qcare.agent.service;

import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.ArthasConnectInfo;
import xyz.firestige.qcare.agent.JvmInfo;

import java.util.List;

public interface ArthasService {
    Mono<List<JvmInfo>> getJvmInfos();
    Mono<ArthasConnectInfo> attachArthasToJvm(String pid);
}
