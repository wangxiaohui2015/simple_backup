package com.my.simplebackup.backup.statistics;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.record.RecordItem;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.TimeUtil;
import com.my.simplebackup.common.statistics.StatisticsEntity;

/**
 * Backup statistics helper.
 */
public class BackupStatisticsHelper {

    private static Logger logger = Logger.getLogger(BackupStatisticsHelper.class);

    public static void statsBackupResults(StatisticsEntity entity, List<RecordItem> items) {
        for (RecordItem item : items) {
            if (item.isBackupSucceed()) {
                entity.setSucceedFiles(entity.getSucceedFiles() + 1);
                entity.setSucceedFileSize(entity.getSucceedFileSize() + item.getSrcFileSize());
                entity.setSucceedTargetFileSize(entity.getSucceedTargetFileSize() + item.getDestFileSize());
            } else {
                entity.setFailedFiles(entity.getFailedFiles() + 1);
                entity.setFailedFileSize(entity.getFailedFileSize() + item.getSrcFileSize());
            }
        }

        entity.setTotalFiles(entity.getSucceedFiles() + entity.getFailedFiles());
        entity.setTotalFileSize(entity.getSucceedFileSize() + entity.getFailedFileSize());
        entity.calculateRate();
    }

    public static void showStatInformation(StatisticsEntity entity) {
        logger.info("================ Backup Statistics ================");
        logger.info("Backup start time: " + TimeUtil.parseDateToStr(new Date(entity.getStartTime())));
        logger.info("Backup end time: " + TimeUtil.parseDateToStr(new Date(entity.getEndTime())));
        logger.info("Backup time taken: " + TimeUtil.calculateElapsedTime(entity.getEndTime(), entity.getStartTime()));
        logger.info("Backup rate: " + FileUtil.getFileSizeString(entity.getRate(), false) + "/s");
        logger.info("Total files: " + entity.getTotalFiles());
        logger.info("Total files size: " + FileUtil.getFileSizeString(entity.getTotalFileSize()));
        logger.info("Succeed files: " + entity.getSucceedFiles());
        logger.info("Succeed files size: " + FileUtil.getFileSizeString(entity.getSucceedFileSize()));
        logger.info("Failed files: " + entity.getFailedFiles());
        logger.info("Failed files size: " + FileUtil.getFileSizeString(entity.getFailedFileSize()));
        logger.info("Succeed target files size: " + FileUtil.getFileSizeString(entity.getSucceedTargetFileSize()));
    }
}
