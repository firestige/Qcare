package xyz.firestige.qcare.server.core.cluster;

import org.springframework.stereotype.Service;

/**
 * Stub implementation of ClusterManagementService for testing purposes.
 * This class does not implement any functionality and is used as a placeholder.
 * TODO: Implement standalone and cluster two way
 */
@Service
public class ClusterManagementServiceStub implements ClusterManagementService{

    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public String getLeaderHost() {
        return "localhost";
    }

}
