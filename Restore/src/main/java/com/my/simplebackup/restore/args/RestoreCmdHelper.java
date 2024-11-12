package com.my.simplebackup.restore.args;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.restore.args.RestoreParameter.MODE_TYPE;

public class RestoreCmdHelper {

    public static final String OPTION_H = "h";
    public static final String OPTION_V = "v";
    public static final String OPTION_K = "k";
    public static final String OPTION_S = "s";
    public static final String OPTION_D = "d";
    public static final String OPTION_T = "t";
    public static final String OPTION_M = "m";
    public static final String OPTION_F = "f";

    private static final String USAGE = "./restore.sh -k <restore_key> -s <source_dir> -d <destination_dir> [-t <threads> | -m <metadata,fake,restore> | -f <filter_path>]";
    private static final int RESTORE_THREAD_MAX = 128;

    private static final String MODE_TYPE_METADATA = "metadata";
    private static final String MODE_TYPE_FAKE = "fake";
    private static final String MODE_TYPE_RESTORE = "restore";

    private static final String MISSING_OPTION_MSG = "Missing required option: ";

    public static Options getCmdOptions() {
        Options options = new Options();
        options.addOption(OPTION_H, "help", false, "Show help.");
        options.addOption(OPTION_V, "version", false, "Show version.");
        options.addOption(OPTION_K, "key", true, "Key used for restore.");
        options.addOption(OPTION_S, "source", true, "Source folder path.");
        options.addOption(OPTION_D, "destination", true, "Destination folder path.");
        options.addOption(OPTION_T, "threads", true, "Threads number used for restore, default is 3.");
        options.addOption(OPTION_M, "mode", true,
                "Restore mode, value can be: [metadata, fake, restore], default is restore.");
        options.addOption(OPTION_F, "filter", true, "Path filter, default is '', match all path.");
        return options;
    }

    public static void printHelpMsg(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.printHelp(USAGE, options);
    }

    public static void printVersion() {
        System.out.println("Version: " + Constants.SW_VERSION);
    }

    public static RestoreParameter getAndCheckParameter(CommandLine cmd) throws Exception {
        RestoreParameter parameter = new RestoreParameter();

        // Key
        if (!cmd.hasOption(OPTION_K)) {
            throw new IllegalArgumentException(MISSING_OPTION_MSG + OPTION_K);
        }
        parameter.setKeyBytes(cmd.getOptionValue(OPTION_K).getBytes(Constants.UTF_8));

        // Source path
        if (!cmd.hasOption(OPTION_S)) {
            throw new IllegalArgumentException(MISSING_OPTION_MSG + OPTION_S);
        }
        parameter.setSrcPath(cmd.getOptionValue(OPTION_S));
        if (!FileUtil.isDir(parameter.getSrcPath())) {
            throw new IllegalArgumentException(
                    "Source path doesn't exist or isn't a directory, path: " + parameter.getSrcPath());
        }

        // Destination path
        if (!cmd.hasOption(OPTION_D)) {
            throw new IllegalArgumentException(MISSING_OPTION_MSG + OPTION_D);
        }
        parameter.setDestPath(cmd.getOptionValue(OPTION_D));
        if (!FileUtil.isDir(parameter.getDestPath())) {
            throw new IllegalArgumentException(
                    "Destination path doesn't exist or isn't a directory, path: " + parameter.getDestPath());
        }

        // Threads
        if (cmd.hasOption(OPTION_T)) {
            String thread = cmd.getOptionValue(OPTION_T);
            try {
                int threadNum = Integer.parseInt(thread);
                if (threadNum <= 0 || threadNum > RESTORE_THREAD_MAX) {
                    throw new IllegalArgumentException(
                            "Invalid thread number: " + thread + ", should be in [1," + RESTORE_THREAD_MAX + "]");
                }
                parameter.setThreads(threadNum);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid thread number: " + thread);
            }
        }

        // Mode
        if (cmd.hasOption(OPTION_M)) {
            String mode = cmd.getOptionValue(OPTION_M);
            if (MODE_TYPE_METADATA.equals(mode)) {
                parameter.setMode(MODE_TYPE.METADATA);
            } else if (MODE_TYPE_FAKE.equals(mode)) {
                parameter.setMode(MODE_TYPE.FAKE);
            } else if (MODE_TYPE_RESTORE.equals(mode)) {
                parameter.setMode(MODE_TYPE.RESTORE);
            } else {
                throw new IllegalArgumentException("Invalid mode type: " + mode);
            }
        }

        // Filter
        if (cmd.hasOption(OPTION_F)) {
            parameter.setFilter(cmd.getOptionValue(OPTION_F));
        }
        return parameter;
    }
}
