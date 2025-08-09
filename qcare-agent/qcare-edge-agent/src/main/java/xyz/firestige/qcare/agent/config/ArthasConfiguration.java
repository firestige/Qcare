package xyz.firestige.qcare.agent.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("qcare.agent.arthas")
public class ArthasConfiguration {
    private String ip;
    private int httpPort;
    private int telnetPort;

    private int sessionTimeout;

    private String username;
    private String password;

    private boolean localConnectionNonAuth;

    private String appName;
    private String tunnelServer;
    private String agentId;

    private String[] disabledCommands;

    private String outputPath;
}
