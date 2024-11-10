package com.my.simplebackup.backup.process;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Backup task controller, used to determine if all tasks are finished or not.
 */
public class BackupTaskController {

    private static Logger logger = Logger.getLogger(BackupTaskController.class);

    private int taskCount = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition allFinish = lock.newCondition();

    /**
     * Invoke this method when submit a new task.
     */
    public void addTask() {
        lock.lock();
        try {
            taskCount++;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invoke this method when a task is finished.
     */
    public void finishTask() {
        lock.lock();
        try {
            taskCount--;
            if (taskCount <= 0) {
                allFinish.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invoke this method after submit all tasks, and wait for all tasks are finished.
     */
    public void await() {
        lock.lock();
        try {
            while (taskCount > 0) {
                try {
                    allFinish.await();
                } catch (InterruptedException e) {
                    logger.error("InterruptedException occurred when await.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
