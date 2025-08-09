package xyz.firestige.qcare.agent.service;

import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.ArthasConnectInfo;
import xyz.firestige.qcare.agent.JvmInfo;

import java.util.List;

@Singleton
public class DefaultArthasService implements ArthasService {
    @Override
    public Mono<List<JvmInfo>> getJvmInfos() {
        return null;
    }

    @Override
    public Mono<ArthasConnectInfo> attachArthasToJvm(String pid) {
        return null;
    }
}
