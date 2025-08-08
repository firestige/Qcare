package xyz.firestige.qcare.server.core.agent.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.firestige.qcare.server.core.ws.CloseStatusWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAgentManager extends AbstractAgentWebSocketManagerSupporter {
    private static final Logger log = LoggerFactory.getLogger(InMemoryAgentManager.class);
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> join(String agentId, WebSocketSession session) {
        sessionMap.compute(agentId, (key, value) -> {
            if (Objects.nonNull(value)) {
                log.warn("Agent [{}] is already joined, replaced session:{} -> session:{}", agentId, value.getId(), session.getId());
                value.close(CloseStatusWrapper.REPLACED).subscribeOn(Schedulers.boundedElastic()).subscribe();
            }
            return session;
        });
        log.info("Agent [{}] joined, session: {}", agentId, session.getId());
        return Mono.empty();
    }

    @Override
    public boolean validateConnectionToken(String token, String agentId) {
        // Todo 暂时不鉴权，后续补齐
        return true;
    }

    @Override
    public void leave(String agentId) {
        sessionMap.remove(agentId);
        log.debug("Agent [{}] is leaving", agentId);
    }

    @Override
    public boolean isConnected(String agentId) {
        return Optional.ofNullable(sessionMap.get(agentId))
                .map(WebSocketSession::isOpen)
                .orElse(false);
    }

    @Nullable
    @Override
    public WebSocketSession getSession(String agentId) {
        return sessionMap.get(agentId);
    }

    @Override
    protected void cleanup() {
        if (sessionMap.isEmpty()) {
            return;
        }

        List<String> removedAgents = new ArrayList<>();
        
        // 使用 removeIf 移除已关闭的连接
        sessionMap.entrySet().removeIf(entry -> {
            String agentId = entry.getKey();
            WebSocketSession session = entry.getValue();
            
            if (session == null || !session.isOpen()) {
                removedAgents.add(agentId);
                return true; // 移除该项
            }
            return false; // 保留该项
        });

        if (!removedAgents.isEmpty()) {
            if (log.isDebugEnabled()) {
                removedAgents.forEach(agentId -> 
                    log.debug("Cleanup: removed closed session for agent [{}]", agentId));
            }
            log.info("Cleanup completed: removed {} closed sessions", removedAgents.size());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Cleanup completed: no closed sessions found");
            }
        }
    }
}
