package xyz.firestige.qcare.agent.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("qcare.agent.arthas")
public class ArthasConfiguration {
    private String path;
    private ArthasProperties properties;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArthasProperties getProperties() {
        return properties;
    }

    public void setProperties(ArthasProperties properties) {
        this.properties = properties;
    }

    @ConfigurationProperties("properties")
    public static class ArthasProperties {
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

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getHttpPort() {
            return httpPort;
        }

        public void setHttpPort(int httpPort) {
            this.httpPort = httpPort;
        }

        public int getTelnetPort() {
            return telnetPort;
        }

        public void setTelnetPort(int telnetPort) {
            this.telnetPort = telnetPort;
        }

        public int getSessionTimeout() {
            return sessionTimeout;
        }

        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isLocalConnectionNonAuth() {
            return localConnectionNonAuth;
        }

        public void setLocalConnectionNonAuth(boolean localConnectionNonAuth) {
            this.localConnectionNonAuth = localConnectionNonAuth;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getTunnelServer() {
            return tunnelServer;
        }

        public void setTunnelServer(String tunnelServer) {
            this.tunnelServer = tunnelServer;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String[] getDisabledCommands() {
            return disabledCommands;
        }

        public void setDisabledCommands(String[] disabledCommands) {
            this.disabledCommands = disabledCommands;
        }

        public String getOutputPath() {
            return outputPath;
        }

        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }
    }

}
