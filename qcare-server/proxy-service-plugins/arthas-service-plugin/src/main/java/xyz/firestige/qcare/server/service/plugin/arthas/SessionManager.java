package xyz.firestige.qcare.server.service.plugin.arthas;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SessionManager {
    private final AtomicReference<String> sessionId = new AtomicReference<>();
    private final Map<String, String> userConsumerMap = new ConcurrentHashMap<>();

    public Mono<String> getOrInitSessionId(ArthasService arthasService) {
        if (sessionId.get() != null) {
            return Mono.just(sessionId.get());
        }
        return arthasService.initSession()
                .map(sid -> {
                    sessionId.set(sid);
                    return sid;
                });
    }

    public Mono<String> getOrJoinConsumerId(String userId, String sessionId, ArthasService arthasService) {
        if (userConsumerMap.containsKey(userId)) {
            return Mono.just(userConsumerMap.get(userId));
        }
        return arthasService.joinSession(sessionId)
                .map(cid -> {
                    userConsumerMap.put(userId, cid);
                    return cid;
                });
    }

    public void clearSession() {
        sessionId.set(null);
        userConsumerMap.clear();
    }
}
