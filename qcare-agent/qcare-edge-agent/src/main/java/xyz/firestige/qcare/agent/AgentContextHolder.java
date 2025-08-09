package xyz.firestige.qcare.agent;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class AgentContextHolder {
    private final Map<String, AgentInfo> agentInfoMap = new ConcurrentHashMap<>();

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

    public AgentInfo getAgentInfo(String agentId) {
        return agentInfoMap.get(agentId);
    }

    public List<AgentInfo> getAgentByName(String agentName) {
        return agentInfoMap.values().stream().filter(agentInfo -> agentInfo.getName().equals(agentName)).collect(Collectors.toList());
    }
}
