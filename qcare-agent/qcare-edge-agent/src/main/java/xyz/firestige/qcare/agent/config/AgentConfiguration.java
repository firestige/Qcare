package xyz.firestige.qcare.agent.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties("qcare.agent")
public class AgentConfiguration {
    private String name = "qcare-edge-agent";
    private String id;
    @NotBlank
    private String remoteAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
