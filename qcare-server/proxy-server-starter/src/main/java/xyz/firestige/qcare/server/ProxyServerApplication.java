package xyz.firestige.qcare.server;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Proxy Server 启动类
 */
@QuarkusMain
public class ProxyServerApplication implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Proxy Server starting...");
        Quarkus.waitForExit();
        return 0;
    }

    public static void main(String[] args) {
        Quarkus.run(ProxyServerApplication.class, args);
    }
}
