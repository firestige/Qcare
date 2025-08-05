package xyz.firestige.qcare.server.service.arthas;

public enum ConnectionState {
    CONNECTED,
    DISCONNECTED,
    RECONNECTING,
    FAILED;

    public boolean isConnected() {
        return this == CONNECTED;
    }

    public boolean isDisconnected() {
        return this == DISCONNECTED;
    }

    public boolean isReconnecting() {
        return this == RECONNECTING;
    }

    public boolean isFailed() {
        return this == FAILED;
    }
}
