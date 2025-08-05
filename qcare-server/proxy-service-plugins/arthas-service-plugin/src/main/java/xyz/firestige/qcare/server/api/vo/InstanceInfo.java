package xyz.firestige.qcare.server.api.vo;

public record InstanceInfo(
        String namespace,
        String instanceId,
        String serviceId,
        String serviceName,
        String host,
        int port,
        String osName,
        String osVersion,
        String arch,
        String jvmVersion
) {
}
