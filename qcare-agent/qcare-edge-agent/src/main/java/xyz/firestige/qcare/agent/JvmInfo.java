package xyz.firestige.qcare.agent;

/**
 * Qcare Edge Agent 中 JVM 的信息
 * <p>
 * 该类用于存储 JVM 的进程 ID 和主类名。
 * </p>
 */
public class JvmInfo {
    private final int pid;
    private final String mainClass;

    /**
     * 构造一个 JvmInfo 实例。
     *
     * @param pid       JVM 的进程 ID
     * @param mainClass JVM 的主类名
     */
    public JvmInfo(String pid, String mainClass) {
        this.pid = Integer.parseInt(pid);
        this.mainClass = mainClass;
    }

    @Override
    public int hashCode() {
        return pid;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JvmInfo that && pid == that.pid;
    }

    @Override
    public String toString() {
        return "JVM[" + pid + ", " + mainClass + "]";
    }
}
