package xyz.firestige.qcare.agent.service;

import org.junit.jupiter.api.Test;
import xyz.firestige.qcare.agent.utils.CommandExecutor;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    @Test
    void execute() throws InterruptedException {
        CommandExecutor executor = new CommandExecutor();
        String command = "jps";
        CountDownLatch latch = new CountDownLatch(1);
        executor.execute("jps", "-l").subscribe(
            output -> System.out.println("Command output: " + output.getOutput()),
            error -> System.err.println("Error executing command: " + error.getMessage()),
            () -> {
                System.out.println("Command execution completed.");
                latch.countDown();
            }
        );
        latch.await();
    }

    @Test
    void testExecute() {
    }

    @Test
    void executeStream() {
        CommandExecutor executor = new CommandExecutor();
        String command = "jps";
        CountDownLatch latch = new CountDownLatch(1);
        executor.executeStream("jps", "-l").subscribe(
            output -> System.out.println("Command output: " + output),
            error -> System.err.println("Error executing command: " + error.getMessage()),
            () -> {
                System.out.println("Command execution completed.");
                latch.countDown();
            }
        );
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Command execution interrupted");
        }
    }

    @Test
    void testExecuteStream() {
    }
}