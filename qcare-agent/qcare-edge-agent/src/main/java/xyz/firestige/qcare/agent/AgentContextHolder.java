package xyz.firestige.qcare.agent;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AgentContextHolder 用于存储和管理 Agent 信息。
 * <p>
 * 该类是单例的，使用 ConcurrentHashMap 来存储 Agent 信息，以确保线程安全。
 * </p>
 */
@Singleton
public class AgentContextHolder {
    private final Map<String, AgentInfo> agentInfoMap = new ConcurrentHashMap<>();

    /**
     * 替换新的AgentInfo
     *
     * @param newInfo 新的 AgentInfo 对象
     */
    public void replaceIfPresent(AgentInfo newInfo) {
        agentInfoMap.put(newInfo.getId(), newInfo);
    }

    public AgentInfo refreshAgentInfo(AgentInfo newAgentInfo) {
        return agentInfoMap.replace(newAgentInfo.getId(), newAgentInfo);
    }

    public AgentInfo initAgent(String agentId, String name, String version, String type, String hostAddress) {
        AgentInfo info = new AgentInfo(agentId, name, type, version, hostAddress);
        agentInfoMap.put(agentId, info);
        return info;
    }

    /**
     * 获取 Agent 信息。
     *
     * @param agentId 代理的唯一标识符
     * <p> *               如果不存在，则返回 null。
     * </p>
     * @return 包含 id = agentId 的 AgentInfo 对象
     */
    public AgentInfo getAgentInfo(String agentId) {
        return agentInfoMap.get(agentId);
    }

    public List<AgentInfo> getAgentByName(String agentName) {
        return agentInfoMap.values().stream()
                .filter(agentInfo -> agentInfo.getName().equals(agentName)).collect(Collectors.toList());
    }
}
