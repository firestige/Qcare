package xyz.firestige.qcare.agent.service;

import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import xyz.firestige.qcare.agent.ArthasConnectInfo;
import xyz.firestige.qcare.agent.JvmInfo;
import xyz.firestige.qcare.agent.config.ArthasConfiguration;
import xyz.firestige.qcare.agent.utils.CommandExecutor;
import xyz.firestige.qcare.agent.utils.JDKTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class DefaultArthasService implements ArthasService {
    private static final Logger log = LoggerFactory.getLogger(DefaultArthasService.class);
    @Inject
    private ArthasConfiguration config;

    @Override
    public Mono<List<JvmInfo>> getJvmInfos() {
        return JDKTools.getJvmPID().map(args -> new JvmInfo(args.get(0), args.get(1)))
                .onErrorContinue((e, o) -> log.error("getJvmInfos error", e))
                .collect(Collectors.toList());
    }

    @Override
    public Mono<ArthasConnectInfo> attachArthasToJvm(String pid) {
        boolean isInstalled = ensureArthasInstalled(config.getPath());
        if (!isInstalled) {
            log.error("Arthas is not installed, cannot attach to JVM with PID: {}", pid);
            return Mono.error(new RuntimeException("Arthas is not installed"));
        }
        boolean isConfigured = ensureArthasConfigured(config.getPath(), config.getProperties());
        if (!isConfigured) {
            log.error("Arthas is not configured, cannot attach to JVM with PID: {}", pid);
            return Mono.error(new RuntimeException("Arthas is not configured"));
        }
        String arthasJarPath = config.getPath() + "/arthas-boot.jar";
        return JDKTools.runJarWithArgs(arthasJarPath, pid, ">", "arthas.log", "2>&1", "&").then(createArthasConnectInfo(config.getProperties()));
    }

    private boolean ensureArthasInstalled(String path) {
        File arthasDir = new File(path);
        File arthasJar = new File(path + "/arthas-boot.jar");
        return arthasDir.exists() && arthasJar.exists();
    }

    private boolean ensureArthasConfigured(String path, ArthasConfiguration.ArthasProperties properties) {
        String propertiesFilePath = path + "/arthas.properties";
        try {
            File propertiesFile = new File(propertiesFilePath);
            Properties existingProps = new Properties();
            List<String> originalLines = new ArrayList<>();

            // 读取现有文件（保留注释）
            if (propertiesFile.exists()) {
                try (BufferedReader reader = Files.newBufferedReader(propertiesFile.toPath(), StandardCharsets.UTF_8)) {
                    originalLines = reader.lines().collect(Collectors.toList());
                    // 加载现有属性
                    try (InputStream is = Files.newInputStream(propertiesFile.toPath())) {
                        existingProps.load(is);
                    }
                }
            }

            // 合并新配置
            Properties mergedProps = new Properties();
            mergedProps.putAll(existingProps);
            if (properties.getIp() != null) mergedProps.setProperty("arthas.ip", properties.getIp());
            if (properties.getHttpPort() > 1000) mergedProps.setProperty("arthas.http-port", String.valueOf(properties.getHttpPort()));
            if (properties.getUsername() != null) mergedProps.setProperty("arthas.username", properties.getUsername());
            if (properties.getPassword() != null) mergedProps.setProperty("arthas.password", properties.getPassword());

            // 写回文件（保留注释）
            List<String> newLines = new ArrayList<>();
            Set<String> updatedKeys = new HashSet<>();

            for (String line : originalLines) {
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    newLines.add(line); // 保留注释和空行
                } else {
                    String key = line.split("=")[0].trim();
                    if (mergedProps.containsKey(key)) {
                        newLines.add(key + "=" + mergedProps.getProperty(key));
                        updatedKeys.add(key);
                    } else {
                        newLines.add(line);
                    }
                }
            }

            // 添加新属性
            for (String key : mergedProps.stringPropertyNames()) {
                if (!updatedKeys.contains(key)) {
                    newLines.add(key + "=" + mergedProps.getProperty(key));
                }
            }

            // 确保目录存在
            propertiesFile.getParentFile().mkdirs();

            // 写入文件
            try (BufferedWriter writer = Files.newBufferedWriter(propertiesFile.toPath(), StandardCharsets.UTF_8)) {
                for (String line : newLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            return true;
        } catch (IOException e) {
            log.error("Failed to configure arthas properties file: {}", propertiesFilePath, e);
            return false;
        }
    }

    private Mono<ArthasConnectInfo> createArthasConnectInfo(ArthasConfiguration.ArthasProperties properties) {
        String url = UriBuilder.of("http://" + properties.getIp() + ":" + properties.getHttpPort())
                .path("/api")
                .queryParam("username", properties.getUsername())
                .queryParam("password", properties.getPassword())
                .build()
                .toString();
        return Mono.just(new ArthasConnectInfo(url));
    }
}
