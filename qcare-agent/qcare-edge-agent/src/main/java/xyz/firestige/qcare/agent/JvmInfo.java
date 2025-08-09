package xyz.firestige.qcare.agent;

public class JvmInfo {
    private final String pid;
    private final String mainClass;

    public JvmInfo(String pid, String mainClass) {
        this.pid = pid;
        this.mainClass = mainClass;
    }

    @Override
    public int hashCode() {
        return pid;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JvmInfo that && this.pid == that.pid;
    }

    @Override
    public String toString() {
        return "JVM[" + pid + ", " + mainClass + "]";
    }
}
