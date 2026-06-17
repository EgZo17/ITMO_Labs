package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import com.labwork.server.core.CommandManager;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerInfo implements Command {

    @Override
    public Response execute(Request request) {
        StringBuilder info = new StringBuilder();
        
        info.append("\n---SERVER STATUS REPORT---\n\n");

        info.append("GENERAL INFORMATION\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("Server start time: ").append(getServerStartTime()).append("\n");
        info.append("Current time: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        info.append("Uptime: ").append(getUptime()).append("\n\n");

        info.append("COLLECTION STATISTICS\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        LabCollection collection = LabCollection.getInstance();
        info.append("Collection size: ").append(collection.getCollection().size()).append(" elements\n");
        info.append("Collection type: ").append(collection.getCollection().getClass().getSimpleName()).append("\n");
        info.append("Is empty: ").append(collection.getCollection().isEmpty()).append("\n\n");

        info.append("ELEMENT STATISTICS\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        if (!collection.getCollection().isEmpty()) {
            info.append("Total minimal points sum: ").append(calculateTotalMinimalPoint()).append("\n");
            info.append("Average minimal point: ").append(calculateAverageMinimalPoint()).append("\n");
            info.append("Min element: ").append(findMinElement()).append("\n");
            info.append("Max element: ").append(findMaxElement()).append("\n");
        } else {
            info.append("Collection is empty\n");
        }
        info.append("\n");

        info.append("JVM INFORMATION\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        Runtime runtime = Runtime.getRuntime();
        info.append("Available processors: ").append(runtime.availableProcessors()).append("\n");
        info.append("Free memory: ").append(runtime.freeMemory() / 1024 / 1024).append(" MB\n");
        info.append("Total memory: ").append(runtime.totalMemory() / 1024 / 1024).append(" MB\n");
        info.append("Max memory: ").append(runtime.maxMemory() / 1024 / 1024).append(" MB\n");
        info.append("Used memory: ").append((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024).append(" MB\n");
        info.append("JVM name: ").append(System.getProperty("java.vm.name")).append("\n");
        info.append("JVM version: ").append(System.getProperty("java.version")).append("\n\n");

        info.append("OPERATING SYSTEM\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("OS name: ").append(System.getProperty("os.name")).append("\n");
        info.append("OS version: ").append(System.getProperty("os.version")).append("\n");
        info.append("OS arch: ").append(System.getProperty("os.arch")).append("\n\n");

        info.append("THREAD INFORMATION\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        info.append("Active threads: ").append(threadBean.getThreadCount()).append("\n");
        info.append("Daemon threads: ").append(countDaemonThreads()).append("\n");
        info.append("Peak thread count: ").append(threadBean.getPeakThreadCount()).append("\n");
        info.append("Total started threads: ").append(threadBean.getTotalStartedThreadCount()).append("\n\n");

        info.append("COMMAND STATISTICS\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("Commands executed: ").append(getCommandsExecuted()).append("\n\n");

        info.append("═══════════════════════════════════════════════════════════\n");
        info.append("Report generated at ").append(new Date()).append("\n");

        return new Response(true, info.toString());
    }

    // Вспомогательные методы
    private String getServerStartTime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        long startTime = System.currentTimeMillis() - uptime;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(startTime));
    }

    private String getUptime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        long seconds = uptime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%d days, %d hours, %d minutes, %d seconds", 
                           days, hours % 24, minutes % 60, seconds % 60);
    }

    private double calculateTotalMinimalPoint() {
        return LabCollection.getInstance().getCollection().stream()
                           .mapToDouble(w -> w.getMinimalPoint())
                           .sum();
    }

    private double calculateAverageMinimalPoint() {
        return LabCollection.getInstance().getCollection().stream()
                           .mapToDouble(w -> w.getMinimalPoint())
                           .average()
                           .orElse(0.0);
    }

    private String findMinElement() {
        return LabCollection.getInstance().getCollection().stream()
                           .min((w1, w2) -> Double.compare(w1.getMinimalPoint(), w2.getMinimalPoint()))
                           .map(w -> w.getName() + " (" + w.getMinimalPoint() + ")")
                           .orElse("N/A");
    }

    private String findMaxElement() {
        return LabCollection.getInstance().getCollection().stream()
                           .max((w1, w2) -> Double.compare(w1.getMinimalPoint(), w2.getMinimalPoint()))
                           .map(w -> w.getName() + " (" + w.getMinimalPoint() + ")")
                           .orElse("N/A");
    }

    private int countDaemonThreads() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadBean.getAllThreadIds();
        int daemonCount = 0;
        for (long threadId : threadIds) {
            Thread thread = getThreadById(threadId);
            if (thread != null && thread.isDaemon()) {
                daemonCount++;
            }
        }
        return daemonCount;
    }

    private Thread getThreadById(long id) {
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread thread : threads) {
            if (thread != null && thread.getId() == id) {
                return thread;
            }
        }
        return null;
    }

    private int getCommandsExecuted() {
        return CommandManager.getExecutedCommandsNumber();
    }
}
