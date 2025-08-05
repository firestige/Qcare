package xyz.firestige.qcare.agent.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;

/**
 * 代理配置类
 */
@Singleton
@ConfigurationProperties("qcare.agent")
public class AgentConfiguration {

    private String id;
    private String name;
    private String version;

    @Property(name = "qcare.server.url")
    private String serverUrl;

    @Property(name = "qcare.server.websocket.url")
    private String websocketUrl;

    @Property(name = "qcare.agent.heartbeat.interval", defaultValue = "60")
    private int heartbeatInterval;

    @Property(name = "qcare.agent.monitoring.interval", defaultValue = "30")
    private int monitoringInterval;

    @Property(name = "qcare.agent.health.check.interval", defaultValue = "10")
    private int healthCheckInterval;

    @Property(name = "qcare.agent.reconnect.delay", defaultValue = "5")
    private int reconnectDelay;

    @Property(name = "qcare.agent.max.reconnect.attempts", defaultValue = "10")
    private int maxReconnectAttempts;

    // Getters and Setters
    public String getId() {
        return id != null ? id : "edge-agent-" + System.currentTimeMillis();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "QCare Edge Agent";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version != null ? version : "1.0.0";
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServerUrl() {
        return serverUrl != null ? serverUrl : "http://localhost:8080";
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getWebsocketUrl() {
        return websocketUrl != null ? websocketUrl : "ws://localhost:8080/ws/agent";
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(int monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
    }

    public int getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public void setHealthCheckInterval(int healthCheckInterval) {
        this.healthCheckInterval = healthCheckInterval;
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }

    public void setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public int getMaxReconnectAttempts() {
        return maxReconnectAttempts;
    }

    public void setMaxReconnectAttempts(int maxReconnectAttempts) {
        this.maxReconnectAttempts = maxReconnectAttempts;
    }
}
