package xyz.firestige.qcare.server.service.arthas;

import java.time.Instant;

public class ArthasInstance {
    private final String pid;
    private final String ip;
    private final int port;
    private DeployState deployState;
    private ConnectionState connectionState;
    private Instant startAt;
    private Instant lastUpdateAt;

    public ArthasInstance(String pid, String ip, int port) {
        this.pid = pid;
        this.ip = ip;
        this.port = port;
        this.deployState = DeployState.NOT_DEPLOYED;
        this.connectionState = ConnectionState.DISCONNECTED;
        this.startAt = Instant.now();
        this.lastUpdateAt = Instant.now();
    }

    public String pid() {
        return pid;
    }

    public String ip() {
        return ip;
    }

    public int port() {
        return port;
    }

    public DeployState runningState() {
        return deployState;
    }

    public ArthasInstance setDeployState(DeployState deployState) {
        this.deployState = deployState;
        return this;
    }

    public ConnectionState connectionState() {
        return connectionState;
    }

    public ArthasInstance setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
        return this;
    }

    public Instant startAt() {
        return startAt;
    }

    public ArthasInstance setStartAt(Instant startAt) {
        this.startAt = startAt;
        return this;
    }

    public Instant lastUpdateAt() {
        return lastUpdateAt;
    }

    public ArthasInstance setLastUpdateAt(Instant lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
        return this;
    }
}
