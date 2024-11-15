package com.my.simplebackup.common.statistics;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.TimeUtil;
import com.my.simplebackup.common.task.TaskResult;

/**
 * Backup statistics helper.
 */
public class StatisticsHelper {

    private static Logger logger = Logger.getLogger(StatisticsHelper.class);

    /**
     * Statistic and show backup result.
     * 
     * @param entity StatisticsEntity
     * @param taskResults TaskResult list
     */
    public static void statAndShowBackupResult(StatisticsEntity entity,
                    List<TaskResult> taskResults) {
        showStdAndLog("================ Backup Statistics ================");
        statsTaskResults(entity, taskResults);
        showStatInformation(entity);
    }

    /**
     * Statistic and show restore result.
     * 
     * @param entity StatisticsEntity
     * @param taskResults TaskResult list
     */
    public static void statAndShowRestoreResult(StatisticsEntity entity,
                    List<TaskResult> taskResults) {
        showStdAndLog("================ Restore Statistics ================");
        statsTaskResults(entity, taskResults);
        showStatInformation(entity);
    }

    /**
     * Show message on stdio and log
     * 
     * @param msg message
     */
    public static void showStdAndLog(String msg) {
        System.out.println(msg);
        logger.info(msg);
    }

    /**
     * Show message on stdio.
     * 
     * @param msg message
     */
    public static void prt(String msg) {
        System.out.print(msg);
    }

    /**
     * Show message on stdio.
     * 
     * @param msg message
     */
    public static void prtln(String msg) {
        System.out.println(msg);
    }

    private static void statsTaskResults(StatisticsEntity entity, List<TaskResult> taskResults) {
        for (TaskResult result : taskResults) {
            if (result.isSucceed()) {
                entity.setSucceedFiles(entity.getSucceedFiles() + 1);
                entity.setSucceedFileSize(entity.getSucceedFileSize() + result.getSrcFileSize());
                entity.setSucceedTargetFileSize(
                                entity.getSucceedTargetFileSize() + result.getDestFileSize());
            } else {
                entity.setFailedFiles(entity.getFailedFiles() + 1);
                entity.setFailedFileSize(entity.getFailedFileSize() + result.getSrcFileSize());
            }
        }

        entity.setTotalFiles(entity.getSucceedFiles() + entity.getFailedFiles());
        entity.setTotalFileSize(entity.getSucceedFileSize() + entity.getFailedFileSize());
        entity.calculateRate();
    }

    private static void showStatInformation(StatisticsEntity entity) {
        showStdAndLog("Start time: " + TimeUtil.parseDateToStr(new Date(entity.getStartTime())));
        showStdAndLog("End time: " + TimeUtil.parseDateToStr(new Date(entity.getEndTime())));
        showStdAndLog("Time taken: " + TimeUtil.calculateElapsedTime(entity.getEndTime(),
                        entity.getStartTime()));
        showStdAndLog("Rate: " + FileUtil.getFileSizeString(entity.getRate(), false) + "/s");
        showStdAndLog("Total files: " + entity.getTotalFiles());
        showStdAndLog("Total files size: " + FileUtil.getFileSizeString(entity.getTotalFileSize()));
        showStdAndLog("Succeed files: " + entity.getSucceedFiles());
        showStdAndLog("Succeed files size: "
                        + FileUtil.getFileSizeString(entity.getSucceedFileSize()));
        showStdAndLog("Failed files: " + entity.getFailedFiles());
        showStdAndLog("Failed files size: "
                        + FileUtil.getFileSizeString(entity.getFailedFileSize()));
        showStdAndLog("Succeed target files size: "
                        + FileUtil.getFileSizeString(entity.getSucceedTargetFileSize()));
    }
}
