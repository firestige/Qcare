package xyz.firestige.qcare.agent;

import io.micronaut.runtime.Micronaut;

/**
 * Qcare Edge Agent 主应用程序
 */
public class EdgeAgentApplication {
    /**
     * 主方法，应用程序入口点。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        Micronaut.run(EdgeAgentApplication.class, args);
    }
}
