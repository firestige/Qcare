package xyz.firestige.qcare.agent;

public class AgentInfo {
    private final String id;
    private final String name;
    private final String type;
    private final String version;
    private final String wsHost;

    public AgentInfo(String id, String name, String type, String version, String wsHost) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.version = version;
        this.wsHost = wsHost;
    }

    private AgentInfo(AgentInfo other, String wsHost) {
        this.id = other.id;
        this.name = other.name;
        this.type = other.type;
        this.version = other.version;
        this.wsHost = wsHost;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWsHost() {
        return wsHost;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public AgentInfo copyWithWsHost(String wsHost) {
        return new AgentInfo(this, wsHost);
    }
}
