package com.my.simplebackup.backup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.util.ServiceConfigPropertiesUtil;

/**
 * File encryption executor, use this class to submit all tasks concurrently.
 * 
 * @author Administrator
 */
public class FileEncryptionExecutor {
    private static Logger logger = Logger.getLogger(FileEncryptionExecutor.class);

    private static final FileEncryptionExecutor instance = new FileEncryptionExecutor();

    private ExecutorService service;

    private FileEncryptionExecutor() {
        service = Executors.newFixedThreadPool(
                        ServiceConfigPropertiesUtil.getInstance().getThreadNumber());
    }

    /**
     * Get singleton instance object.
     * 
     * @return
     */
    public static FileEncryptionExecutor getInstance() {
        return instance;
    }

    /**
     * Submit task.
     * 
     * @param task task object
     * @param controller backup task controller object.
     */
    public void submitTask(Runnable task, BackupTaskController controller) {
        if (!service.isShutdown()) {
            controller.addTask();
            service.submit(task);
        } else {
            logger.warn("FileEncryptionExecutor is shutdown, cann't accept task.");
        }
    }

    /**
     * Shutdown executor service, invoke this method before exit JVM.
     */
    public void shutDownExecutorService() {
        logger.info("Shutting down FileEncryptionExecutor...");
        try {
            service.shutdown();
            service.awaitTermination(60 * 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("InterruptedException occurred when shutDownExecutorService.", e);
        }
        logger.info("Shut down FileEncryptionExecutor.");
    }
}
