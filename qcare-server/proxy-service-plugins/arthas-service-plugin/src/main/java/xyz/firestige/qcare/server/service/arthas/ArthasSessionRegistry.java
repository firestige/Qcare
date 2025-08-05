package xyz.firestige.qcare.server.service.arthas;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ArthasSessionRegistry {
    private final Map<UserInstanceKey, String> userInstanceToArthasId = new ConcurrentHashMap<>();
    private final Map<String, UserInstanceKey> arthasIdToUserInstance = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userIdToArthasIds = new ConcurrentHashMap<>();
    private final Map<String, Lock> userLocks = new ConcurrentHashMap<>();

    private Lock getLock(String userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    public void register(String userId, String instanceId, String arthasId) {
        Lock lock = getLock(userId);
        lock.lock();
        try {
            UserInstanceKey key = new UserInstanceKey(userId, instanceId);
            userInstanceToArthasId.put(key, arthasId);
            arthasIdToUserInstance.put(arthasId, key);
            userIdToArthasIds.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(arthasId);
        } finally {
            lock.unlock();
        }
    }

    public String getArthasId(String userId, String instanceId) {
        return userInstanceToArthasId.get(new UserInstanceKey(userId, instanceId));
    }

    public UserInstanceKey getUserInstance(String arthasId) {
        return arthasIdToUserInstance.get(arthasId);
    }

    public Set<String> getArthasIdsByUser(String userId) {
        return userIdToArthasIds.getOrDefault(userId, Collections.emptySet());
    }

    public void unregister(String userId, String instanceId) {
        Lock lock = getLock(userId);
        lock.lock();
        try {
            UserInstanceKey key = new UserInstanceKey(userId, instanceId);
            String arthasId = userInstanceToArthasId.remove(key);
            if (arthasId != null) {
                arthasIdToUserInstance.remove(arthasId);
                Set<String> set = userIdToArthasIds.get(userId);
                if (set != null) {
                    set.remove(arthasId);
                    if (set.isEmpty()) userIdToArthasIds.remove(userId);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static class UserInstanceKey {
        private final String userId;
        private final String instanceId;
        // 构造、equals、hashCode略
        // 需实现 equals 和 hashCode
        public UserInstanceKey(String userId, String instanceId) {
            this.userId = userId;
            this.instanceId = instanceId;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserInstanceKey that = (UserInstanceKey) o;
            return Objects.equals(userId, that.userId) && Objects.equals(instanceId, that.instanceId);
        }
        @Override
        public int hashCode() {
            return Objects.hash(userId, instanceId);
        }
    }
}