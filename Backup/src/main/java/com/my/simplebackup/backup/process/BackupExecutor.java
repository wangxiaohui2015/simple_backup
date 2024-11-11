package com.my.simplebackup.backup.process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.record.RecordItem;

/**
 * Backup executor.
 */
public class BackupExecutor {
    private static Logger logger = Logger.getLogger(BackupExecutor.class);

    private static final int SHUTDOWN_WAIT_SECONDS = 60 * 10;

    private ExecutorService service;

    public BackupExecutor(int threads) {
        service = Executors.newFixedThreadPool(threads);
    }

    /**
     * Submit a backup task.
     * 
     * @param task       backup task
     * @param controller BackupTaskController
     * @return Future<RecordItem>
     */
    public Future<RecordItem> submitTask(Callable<RecordItem> task, BackupTaskController controller) {
        if (!service.isShutdown()) {
            controller.addTask();
            return service.submit(task);
        } else {
            logger.warn("Backup executor is shutdown, cann't accept task.");
        }
        return null;
    }

    /**
     * Shutdown executor service.
     * 
     */
    public void shutDownExecutorService() {
        logger.info("Shutting down backup executor...");
        try {
            service.shutdown();
            service.awaitTermination(SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred when shutting dowm backup executor.", e);
        }
    }
}
