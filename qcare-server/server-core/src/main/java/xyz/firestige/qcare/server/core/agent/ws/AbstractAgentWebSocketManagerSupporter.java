package xyz.firestige.qcare.server.core.agent.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public abstract class AbstractAgentWebSocketManagerSupporter implements AgentWebSocketManager, InitializingBean, DisposableBean {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractAgentWebSocketManagerSupporter.class);
    
    private Duration cleanupInterval = Duration.ofMinutes(5); // 默认5分钟
    private Disposable cleanupTask;

    @Override
    public void afterPropertiesSet() throws Exception {
        initialCleanupTask();
    }

    /**
     * 初始化清理任务，使用Spring共享弹性线程池周期执行cleanup方法
     */
    private void initialCleanupTask() {
        if (cleanupInterval != null && !cleanupInterval.isZero() && !cleanupInterval.isNegative()) {
            log.info("Starting cleanup task with interval: {}", cleanupInterval);
            
            this.cleanupTask = Schedulers.boundedElastic()
                    .schedulePeriodically(
                            this::safeCleanup,
                            cleanupInterval.toMillis(),
                            cleanupInterval.toMillis(),
                            java.util.concurrent.TimeUnit.MILLISECONDS
                    );
        } else {
            log.warn("Cleanup interval is not configured or invalid, cleanup task will not be scheduled");
        }
    }

    /**
     * 安全执行cleanup方法，捕获异常避免任务中断
     */
    private void safeCleanup() {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Executing periodic cleanup task");
            }
            cleanup();
        } catch (Exception e) {
            log.error("Error during cleanup execution", e);
            // 不重新抛出异常，避免中断周期任务
        }
    }

    /**
     * 设置清理间隔
     */
    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    /**
     * 获取清理间隔
     */
    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    @Override
    public void destroy() throws Exception {
        if (cleanupTask != null && !cleanupTask.isDisposed()) {
            log.info("Disposing cleanup task");
            cleanupTask.dispose();
        }
    }

    /**
     * 子类实现具体的清理逻辑
     */
    protected abstract void cleanup();
}
