package xyz.firestige.qcare.agent.utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {
    
    private final ProcessBuilderFactory processBuilderFactory;
    
    public CommandExecutor() {
        this.processBuilderFactory = new ProcessBuilderFactory();
    }
    
    /**
     * 响应式执行命令并返回结果
     */
    public Mono<CommandResult> execute(String... command) {
        return execute(Arrays.asList(command));
    }
    
    public Mono<CommandResult> execute(List<String> command) {
        return processBuilderFactory.create()
                .command(command)
                .execute();
    }
    
    /**
     * 流式执行命令，实时返回输出行
     */
    public Flux<String> executeStream(String... command) {
        return executeStream(Arrays.asList(command));
    }
    
    public Flux<String> executeStream(List<String> command) {
        return processBuilderFactory.create()
                .command(command)
                .executeStream();
    }
    
    /**
     * ProcessBuilder工厂类
     */
    public static class ProcessBuilderFactory {
        
        public ProcessBuilderWrapper create() {
            return new ProcessBuilderWrapper();
        }
    }
    
    /**
     * ProcessBuilder包装器，提供链式调用
     */
    public static class ProcessBuilderWrapper {
        private final ProcessBuilder processBuilder;
        private Duration timeout = Duration.ofSeconds(30);
        private boolean inheritIO = false;
        
        public ProcessBuilderWrapper() {
            this.processBuilder = new ProcessBuilder();
        }
        
        public ProcessBuilderWrapper command(String... command) {
            processBuilder.command(command);
            return this;
        }
        
        public ProcessBuilderWrapper command(List<String> command) {
            processBuilder.command(command);
            return this;
        }
        
        public ProcessBuilderWrapper directory(File directory) {
            processBuilder.directory(directory);
            return this;
        }
        
        public ProcessBuilderWrapper environment(Map<String, String> env) {
            processBuilder.environment().putAll(env);
            return this;
        }
        
        public ProcessBuilderWrapper environment(String key, String value) {
            processBuilder.environment().put(key, value);
            return this;
        }
        
        public ProcessBuilderWrapper timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public ProcessBuilderWrapper timeout(long seconds) {
            this.timeout = Duration.ofSeconds(seconds);
            return this;
        }
        
        public ProcessBuilderWrapper inheritIO() {
            this.inheritIO = true;
            return this;
        }
        
        public ProcessBuilderWrapper redirectErrorStream(boolean redirectErrorStream) {
            processBuilder.redirectErrorStream(redirectErrorStream);
            return this;
        }
        
        /**
         * 响应式执行命令
         */
        public Mono<CommandResult> execute() {
            return Mono.fromCallable(() -> {
                if (inheritIO) {
                    processBuilder.inheritIO();
                }
                return processBuilder.start();
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(this::processExecution)
            .timeout(timeout)
            .onErrorMap(Exception.class, e -> {
                if (e instanceof java.util.concurrent.TimeoutException) {
                    return new CommandTimeoutException("命令执行超时: " + timeout.getSeconds() + "秒");
                }
                return e;
            })
            .onErrorReturn(CommandTimeoutException.class, 
                CommandResult.timeout(timeout.getSeconds()))
            .onErrorResume(Exception.class, e ->
                Mono.just(CommandResult.error("执行命令时发生异常: " + e.getMessage(), e)));
        }
        
        /**
         * 流式执行命令，实时返回输出
         */
        public Flux<String> executeStream() {
            return Mono.fromCallable(() -> processBuilder.start())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(this::createOutputFlux)
                .timeout(timeout)
                .onErrorResume(Exception.class, e -> 
                    Flux.error(new RuntimeException("流式执行命令失败: " + e.getMessage(), e)));
        }
        
        private Mono<CommandResult> processExecution(Process process) {
            if (inheritIO) {
                return waitForProcessCompletion(process)
                    .map(exitCode -> CommandResult.success(exitCode, "", ""));
            } else {
                return processWithOutput(process);
            }
        }
        
        private Mono<CommandResult> processWithOutput(Process process) {
            Mono<String> outputMono = readStreamAsString(process.getInputStream());
            Mono<String> errorMono = readStreamAsString(process.getErrorStream());
            Mono<Integer> exitCodeMono = waitForProcessCompletion(process);
            
            return Mono.zip(outputMono, errorMono, exitCodeMono)
                .map(tuple -> CommandResult.success(tuple.getT3(), tuple.getT1(), tuple.getT2()));
        }
        
        private Flux<String> createOutputFlux(Process process) {
            Flux<String> outputFlux = readStreamAsFlux(process.getInputStream());
            Flux<String> errorFlux = readStreamAsFlux(process.getErrorStream())
                .map(line -> "[ERROR] " + line);
            
            return Flux.merge(outputFlux, errorFlux)
                .doFinally(signalType -> {
                    if (process.isAlive()) {
                        process.destroyForcibly();
                    }
                });
        }
        
        private Mono<String> readStreamAsString(InputStream inputStream) {
            return Flux.fromStream(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
            )
            .subscribeOn(Schedulers.boundedElastic())
            .collectList()
            .map(lines -> String.join(System.lineSeparator(), lines))
            .doFinally(signalType -> {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            });
        }
        
        private Flux<String> readStreamAsFlux(InputStream inputStream) {
            return Flux.fromStream(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
            )
            .subscribeOn(Schedulers.boundedElastic())
            .doFinally(signalType -> {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 忽略关闭异常
                }
            });
        }
        
        private Mono<Integer> waitForProcessCompletion(Process process) {
            return Mono.fromCallable(() -> {
                boolean finished = process.waitFor(timeout.getSeconds(), TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    throw new CommandTimeoutException("进程执行超时");
                }
                return process.exitValue();
            })
            .subscribeOn(Schedulers.boundedElastic());
        }
    }
    
    /**
     * 命令执行结果
     */
    public static class CommandResult {
        private final boolean success;
        private final int exitCode;
        private final String output;
        private final String error;
        private final String errorMessage;
        private final Exception exception;
        private final boolean timeout;
        private final long timeoutSeconds;
        
        private CommandResult(boolean success, int exitCode, String output, String error, 
                             String errorMessage, Exception exception, boolean timeout, long timeoutSeconds) {
            this.success = success;
            this.exitCode = exitCode;
            this.output = output != null ? output : "";
            this.error = error != null ? error : "";
            this.errorMessage = errorMessage;
            this.exception = exception;
            this.timeout = timeout;
            this.timeoutSeconds = timeoutSeconds;
        }
        
        public static CommandResult success(int exitCode, String output, String error) {
            return new CommandResult(exitCode == 0, exitCode, output, error, null, null, false, 0);
        }
        
        public static CommandResult error(String errorMessage, Exception exception) {
            return new CommandResult(false, -1, "", "", errorMessage, exception, false, 0);
        }
        
        public static CommandResult timeout(long timeoutSeconds) {
            return new CommandResult(false, -1, "", "", 
                    "命令执行超时: " + timeoutSeconds + "秒", null, true, timeoutSeconds);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public int getExitCode() { return exitCode; }
        public String getOutput() { return output; }
        public String getError() { return error; }
        public String getErrorMessage() { return errorMessage; }
        public Exception getException() { return exception; }
        public boolean isTimeout() { return timeout; }
        public long getTimeoutSeconds() { return timeoutSeconds; }
        
        @Override
        public String toString() {
            return String.format("CommandResult{success=%s, exitCode=%d, output='%s', error='%s'}", 
                    success, exitCode, output.trim(), error.trim());
        }
    }
    
    /**
     * 命令超时异常
     */
    public static class CommandTimeoutException extends RuntimeException {
        public CommandTimeoutException(String message) {
            super(message);
        }
    }
}
