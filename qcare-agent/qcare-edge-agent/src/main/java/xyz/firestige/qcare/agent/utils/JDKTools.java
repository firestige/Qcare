package xyz.firestige.qcare.agent.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public final class JDKTools {
    private static final CommandExecutor EXECUTOR = new CommandExecutor();

    public static Mono<Void> runJarWithArgs(String jarPath, String... args) {
        List<String> argList = List.of("java", "-jar", jarPath);
        argList.addAll(List.of(args));
        return EXECUTOR.execute(argList).then();
    }

    public static Flux<List<String>> getJvmPID() {
        return EXECUTOR.executeStream("jps", "-l")
                .flatMap(line -> Flux.fromArray(line.split(" ")).concatWithValues("").take(2).collect(Collectors.toList()));
    }
}
