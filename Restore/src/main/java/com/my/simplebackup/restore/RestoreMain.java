package com.my.simplebackup.restore;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.StringUtil;
import com.my.simplebackup.common.metadata.FileMetadata;
import com.my.simplebackup.common.metadata.FileMetadataHelper;
import com.my.simplebackup.common.statistics.StatisticsEntity;
import com.my.simplebackup.common.statistics.StatisticsHelper;
import com.my.simplebackup.common.task.TaskResult;
import com.my.simplebackup.restore.args.RestoreCmdHelper;
import com.my.simplebackup.restore.args.RestoreParameter;
import com.my.simplebackup.restore.task.MetadataDecryptResult;
import com.my.simplebackup.restore.task.MetadataTaskThread;
import com.my.simplebackup.restore.task.RestoreTaskThread;

/**
 * The main entry to restore files.
 */
public class RestoreMain {

    private static Logger logger;

    private ExecutorService metadataExecutor;
    private ExecutorService taskExecutor;

    public static void main(String[] args) {
        new RestoreMain(args).startRestore(args);
    }

    public RestoreMain(String[] args) {
        init(args);
    }

    private void startRestore(String[] args) {
        try {
            logger.info("Begin to execute restore task.");

            // Resolve parameters
            RestoreParameter parameter = resolveRestoreParameter(args);

            // Initialize executors
            this.metadataExecutor = Executors.newFixedThreadPool(parameter.getThreads());
            this.taskExecutor = Executors.newFixedThreadPool(parameter.getThreads());

            // Start restore task according to mode type
            List<TaskResult> taskResults;
            if (parameter.getMode() == RestoreParameter.MODE_TYPE.METADATA) {
                saveMetadata(parameter);
            } else if (parameter.getMode() == RestoreParameter.MODE_TYPE.FAKE) {
                StatisticsEntity statEntity = new StatisticsEntity();
                statEntity.startStat();
                taskResults = restoreFile(parameter, true);
                statEntity.endStat();
                StatisticsHelper.statAndShowRestoreResult(statEntity, taskResults);
            } else {
                StatisticsEntity statEntity = new StatisticsEntity();
                statEntity.startStat();
                taskResults = restoreFile(parameter, false);
                statEntity.endStat();
                StatisticsHelper.statAndShowRestoreResult(statEntity, taskResults);
            }
        } catch (Exception e) {
            logger.info("Exception occurred to execute restore task.", e);
        } finally {
            this.metadataExecutor.shutdown();
            this.taskExecutor.shutdown();
            logger.info("End to execute restore task.");
        }
    }

    private void saveMetadata(RestoreParameter parameter) throws Exception {
        List<MetadataDecryptResult> retList = getMetadataRetList(parameter);
        List<FileMetadata> metadataList = new ArrayList<FileMetadata>();
        for (MetadataDecryptResult ret : retList) {
            metadataList.add(ret.getMetadata());
        }
        String destPath = parameter.getDestPath() + File.separator + "metadata_" + new Date().getTime() + ".json";
        FileMetadataHelper.saveMetadataListToFile(metadataList, destPath);
        logger.info("Succeed to save metadata, file path: " + destPath);
    }

    private List<TaskResult> restoreFile(RestoreParameter parameter, boolean isFake) throws Exception {
        List<TaskResult> taskResults = new ArrayList<TaskResult>();
        List<Future<TaskResult>> futureList = new ArrayList<Future<TaskResult>>();

        List<MetadataDecryptResult> retList = getMetadataRetList(parameter);
        for (MetadataDecryptResult ret : retList) {
            RestoreTaskThread task = new RestoreTaskThread(parameter.getKeyBytes(), parameter.getDestPath(), ret,
                    isFake);
            Future<TaskResult> future = taskExecutor.submit(task);
            futureList.add(future);
        }

        // Wait all task to be finished and get results
        for (Future<TaskResult> future : futureList) {
            taskResults.add(future.get());
        }
        return taskResults;
    }

    private void getMetadataDecryptRetFutureList(String srcPath, byte[] keyBytes,
            List<Future<MetadataDecryptResult>> futureList) throws Exception {
        File sourceFile = new File(srcPath);
        File[] files = sourceFile.listFiles();
        if (null == files) {
            logger.error("files is null, cannot process backup task, source dir: " + srcPath);
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                MetadataTaskThread task = new MetadataTaskThread(keyBytes, file.getAbsolutePath());
                Future<MetadataDecryptResult> future = this.metadataExecutor.submit(task);
                futureList.add(future);
            } else {
                getMetadataDecryptRetFutureList(file.getAbsolutePath(), keyBytes, futureList);
            }
        }
    }

    private List<MetadataDecryptResult> getMetadataRetList(RestoreParameter parameter) throws Exception {
        List<MetadataDecryptResult> retList = new ArrayList<MetadataDecryptResult>();
        List<Future<MetadataDecryptResult>> futureList = new ArrayList<Future<MetadataDecryptResult>>();
        getMetadataDecryptRetFutureList(parameter.getSrcPath(), parameter.getKeyBytes(), futureList);

        // Wait task to complete, and then filter MetadataDecryptRet
        for (Future<MetadataDecryptResult> future : futureList) {
            MetadataDecryptResult metadataRet = future.get();
            FileMetadata metadata = metadataRet.getMetadata();
            if (null == metadata) {
                continue;
            }
            if (StringUtil.isEmpty(metadata.getFileFullPath())) {
                continue;
            }
            if (!metadata.getFileFullPath().startsWith(parameter.getFilter())) {
                continue;
            }
            retList.add(metadataRet);
        }
        return retList;
    }

    private RestoreParameter resolveRestoreParameter(String[] args) {
        Options options = RestoreCmdHelper.getCmdOptions();
        RestoreParameter parameter = null;
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(RestoreCmdHelper.OPTION_H)) {
                RestoreCmdHelper.printHelpMsg(options);
                System.exit(0);
            } else if (cmd.hasOption(RestoreCmdHelper.OPTION_V)) {
                RestoreCmdHelper.printVersion();
                System.exit(0);
            }
            parameter = RestoreCmdHelper.getAndCheckParameter(cmd);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            RestoreCmdHelper.printHelpMsg(options);
            System.exit(-1);
        }
        return parameter;
    }

    private void init(String[] args) {
        String rootDir = System.getProperty(Constants.MAIN_ROOT_DIR_KEY);
        if (StringUtil.isEmpty(rootDir) || !new File(rootDir).isDirectory()) {
            System.out.println("ERROR: rootDir is not set or not exist, system exits.");
            System.exit(-1);
        }
        try {
            logger = Logger.getLogger(RestoreMain.class);
        } catch (Exception e) {
            System.out.println("Failed to initialize logger.");
            System.exit(-1);
        }
    }

}
