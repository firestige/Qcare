package xyz.firestige.qcare.server.proto.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class ApplicationEvent {
    private final String id;
    private final String name;
    private final Instant timestamp;

    public ApplicationEvent(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.timestamp = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract Object getPayload();

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ApplicationEvent that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
