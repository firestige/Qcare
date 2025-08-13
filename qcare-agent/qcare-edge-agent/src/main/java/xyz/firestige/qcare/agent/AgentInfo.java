package xyz.firestige.qcare.agent;

/**
 * 代理信息类
 * <p>
 * 该类用于存储代理的基本信息，包括 ID、名称、类型、版本和 WebSocket 主机地址。
 * </p>
 */
public class AgentInfo {
    private final String id;
    private final String name;
    private final String type;
    private final String version;
    private final String wsHost;

    /**
     * 构造一个 AgentInfo 实例。
     *
     * @param id      代理的唯一标识符
     * @param name    代理的名称
     * @param type    代理的类型
     * @param version 代理的版本
     * @param wsHost  WebSocket 主机地址
     */
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

    /**
     * 获取代理的唯一标识符。
     *
     * @return 代理的 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取代理的名称。
     *
     * @return 代理的名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取代理的 WebSocket 主机地址。
     *
     * @return WebSocket 主机地址
     */
    public String getWsHost() {
        return wsHost;
    }

    /**
     * 获取代理的类型。
     *
     * @return 代理的类型
     */
    public String getType() {
        return type;
    }

    /**
     * 获取代理的版本。
     *
     * @return 代理的版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * 生成一个新的 AgentInfo 实例，使用指定的 WebSocket 主机地址。
     *
     * @param wsHost 新的 WebSocket 主机地址
     * @return 一个新的 AgentInfo 实例
     */
    public AgentInfo copyWithWsHost(String wsHost) {
        return new AgentInfo(this, wsHost);
    }
}
