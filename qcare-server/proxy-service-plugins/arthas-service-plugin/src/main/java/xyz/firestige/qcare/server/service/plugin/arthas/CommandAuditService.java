package xyz.firestige.qcare.server.service.plugin.arthas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CommandAuditService {
    private static final Logger logger = LoggerFactory.getLogger(CommandAuditService.class);

    public void audit(String userId, String action, String command, Map<String, Object> result) {
        logger.info("[ArthasAudit] userId={}, time={}, action={}, command={}, resultState={}",
                userId, LocalDateTime.now(), action, command, result != null ? result.get("state") : null);
    }
}

