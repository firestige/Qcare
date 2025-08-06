package xyz.firestige.qcare.server.service.arthas;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultArthasTunnelService implements ArthasRemoteService {

    private final Map<String, String> instanceMap = new ConcurrentHashMap<>();

    @EventListener(classes = AgentRegisteredEvent.class)
    public void onAgentRegistered(AgentRegisteredEvent event) {
        // 监听 AgentRegisteredEvent 事件，当有新的Agent注册时，对这个实例执行初始化处理
    }

    @EventListener(classes = AgentUnRegisteredEvent.class)
    public void onAgentRegistered(AgentUnRegisteredEvent event) {
        // 监听 AgentUnRegisteredEvent 事件，当有Agent离线时，清理相关的连接数据
    }

    public void initArthasInstances() {
        // 1. 查询Agent注册表，获取所有已注册的Agent实例
        // 2. 把支持arthas部署的实例加入管理清单，下发最新的arthas.properties配置
        // 3. 对每个Agent实例，请求ps -ef | grep java列表
        // 4. 对每个Java进程，获取Arthas实例的连接信息
        // 5. 初始化Arthas实例，存储在管理器中
        return null;
    }

    @Override
    public Mono<List<ArthasInstance>> getAllArthasInstances() {
        return null;
    }

    @Override
    public Mono<Void> connectToArthasConsole(String instanceId) {
        WebClient client = getClient(instanceId);
        String sessionId = getSessionId(instanceId);
        if (sessionId == null) {
            response = initSession(instanceId, client);
            updateSessionId(instanceId, response);
            updateConsumerId(instanceId, response);
        } else {
            response = joinSession(instanceId, client, sessionId);
            updateSessionId(instanceId, response);
            updateConsumerId(instanceId, response);
        }
        return Mono.empty();
    }

    private WebClient getClient(String instanceId) {
        return WebClient.create(instanceMap.get(instanceId));
    }

    @Override
    public Mono<Void> disconnectFromArthasConsole(String instanceId) {
        return null;
    }

    @Override
    public Mono<String> executeArthasCommand(String instanceId, String command) {
        return null;
    }

    @Override
    public Mono<Void> submiteAsyncJob(String instanceId, String jobName, String jobCommand) {
        return null;
    }

    @Override
    public Mono<JobResult> getJobResult(String instanceId, String jobId) {
        return null;
    }

    @Override
    public Mono<Void> cancelJob(String instanceId, String jobId) {
        return null;
    }
}
