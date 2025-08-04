package xyz.firestige.qcare.server.core.infra.cluster;

import java.net.URI;

public interface ClusterManagementService {
    boolean isLeader();
    URI getLeaderUri();
}
