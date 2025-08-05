package xyz.firestige.qcare.server.core.infra.cluster;

public interface ClusterManagementService {
    /**
     * 判断当前节点是不是leader节点
     *
     * @return true-是，false-否
     */
    boolean isLeader();

    /**
     * 获取leader节点的Host
     *
     * @return host
     */
    String getLeaderHost();
}
