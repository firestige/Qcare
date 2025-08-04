package xyz.firestige.qcare.server.service.plugin.arthas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/arthas")
public class ArthasApi {
    @Autowired
    private ArthasService arthasService;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private CommandAuditService auditService;

    // 高危命令黑名单
    private static final Set<String> DANGEROUS_COMMANDS = Set.of(
            "shutdown", "stop", "reset", "exit", "session", "close_session"
    );

    @PostMapping(value = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map> proxyArthasApi(@RequestBody Map<String, Object> payload,
                                    @RequestHeader(value = "X-User-Id", required = false) String userId) {
        String action = (String) payload.get("action");
        String command = (String) payload.getOrDefault("command", "");
        // 注入防护
        if (isDangerousCommand(command)) {
            return Mono.just(Map.of("state", "REFUSED", "message", "危险命令被拦截"));
        }
        // 会话共享
        return sessionManager.getOrInitSessionId(arthasService)
                .flatMap(sessionId -> {
                    payload.put("sessionId", sessionId);
                    // consumerId 仅多用户拉取结果/异步命令时需要
                    if (Objects.equals(action, "pull_results") || Objects.equals(action, "async_exec") || Objects.equals(action, "join_session")) {
                        return sessionManager.getOrJoinConsumerId(userId != null ? userId : UUID.randomUUID().toString(), sessionId, arthasService)
                                .map(consumerId -> {
                                    payload.put("consumerId", consumerId);
                                    return payload;
                                });
                    }
                    return Mono.just(payload);
                })
                .flatMap(arthasService::forwardToArthas)
                .doOnNext(result -> auditService.audit(userId, action, command, result));
    }

    private boolean isDangerousCommand(String command) {
        if (command == null) return false;
        String cmd = command.trim().split(" ")[0].toLowerCase();
        return DANGEROUS_COMMANDS.contains(cmd);
    }

    @GetMapping("/Instance")
    public String hello() {
        return "Hello from Arthas API!";
    }

    // 其他 Arthas 相关的 API 接口可以在这里添加
}
