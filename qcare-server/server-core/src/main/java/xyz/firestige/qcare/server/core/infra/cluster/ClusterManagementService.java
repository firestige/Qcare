package xyz.firestige.qcare.server.core.infra.cluster;

public interface ClusterManagementService {
    boolean isLeader();
    String getLeaderHost();
}
