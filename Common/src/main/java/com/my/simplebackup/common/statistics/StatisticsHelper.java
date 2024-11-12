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
     * @param entity      StatisticsEntity
     * @param taskResults TaskResult list
     */
    public static void statAndShowBackupResult(StatisticsEntity entity, List<TaskResult> taskResults) {
        logger.info("================ Backup Statistics ================");
        statsTaskResults(entity, taskResults);
        showStatInformation(entity);
    }

    /**
     * Statistic and show restore result.
     * 
     * @param entity      StatisticsEntity
     * @param taskResults TaskResult list
     */
    public static void statAndShowRestoreResult(StatisticsEntity entity, List<TaskResult> taskResults) {
        logger.info("================ Restore Statistics ================");
        statsTaskResults(entity, taskResults);
        showStatInformation(entity);
    }

    private static void statsTaskResults(StatisticsEntity entity, List<TaskResult> taskResults) {
        for (TaskResult result : taskResults) {
            if (result.isSucceed()) {
                entity.setSucceedFiles(entity.getSucceedFiles() + 1);
                entity.setSucceedFileSize(entity.getSucceedFileSize() + result.getSrcFileSize());
                entity.setSucceedTargetFileSize(entity.getSucceedTargetFileSize() + result.getDestFileSize());
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
        logger.info("Start time: " + TimeUtil.parseDateToStr(new Date(entity.getStartTime())));
        logger.info("End time: " + TimeUtil.parseDateToStr(new Date(entity.getEndTime())));
        logger.info("Time taken: " + TimeUtil.calculateElapsedTime(entity.getEndTime(), entity.getStartTime()));
        logger.info("Rate: " + FileUtil.getFileSizeString(entity.getRate(), false) + "/s");
        logger.info("Total files: " + entity.getTotalFiles());
        logger.info("Total files size: " + FileUtil.getFileSizeString(entity.getTotalFileSize()));
        logger.info("Succeed files: " + entity.getSucceedFiles());
        logger.info("Succeed files size: " + FileUtil.getFileSizeString(entity.getSucceedFileSize()));
        logger.info("Failed files: " + entity.getFailedFiles());
        logger.info("Failed files size: " + FileUtil.getFileSizeString(entity.getFailedFileSize()));
        logger.info("Succeed target files size: " + FileUtil.getFileSizeString(entity.getSucceedTargetFileSize()));
    }
}
