package com.my.simplebackup.restore.args;

import java.io.Console;
import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.statistics.StatisticsHelper;
import com.my.simplebackup.restore.args.RestoreParameter.MODE_TYPE;

public class RestoreCmdHelper {

    public static final String OPTION_H = "h";
    public static final String OPTION_V = "v";
    public static final String OPTION_S = "s";
    public static final String OPTION_D = "d";
    public static final String OPTION_T = "t";
    public static final String OPTION_M = "m";

    private static final String USAGE =
                    "./restore.sh -s <source_dir> -d <destination_dir> [-t <threads> | -m <metadata,fake,restore>]";
    private static final int RESTORE_THREAD_MAX = 128;

    private static final String MODE_TYPE_METADATA = "metadata";
    private static final String MODE_TYPE_FAKE = "fake";
    private static final String MODE_TYPE_RESTORE = "restore";

    private static final String MISSING_OPTION_MSG = "Missing required option: ";

    /**
     * Get command options object.
     * 
     * @return Options
     */
    public static Options getCmdOptions() {
        Options options = new Options();
        options.addOption(OPTION_H, "help", false, "Show help.");
        options.addOption(OPTION_V, "version", false, "Show version.");
        options.addOption(OPTION_S, "source", true, "Source folder path.");
        options.addOption(OPTION_D, "destination", true, "Destination folder path.");
        options.addOption(OPTION_T, "threads", true,
                        "Threads number used for restore, default is 3.");
        options.addOption(OPTION_M, "mode", true,
                        "Restore mode, value can be: [metadata, fake, restore], default is restore.");
        return options;
    }

    private static void printHelpMsg(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.printHelp(USAGE, options);
    }

    private static void printVersion() {
        System.out.println("Version: " + Constants.SW_VERSION);
    }

    private static RestoreParameter getAndCheckParameter(CommandLine cmd) throws Exception {
        RestoreParameter parameter = new RestoreParameter();

        // Source path
        if (!cmd.hasOption(OPTION_S)) {
            throw new IllegalArgumentException(MISSING_OPTION_MSG + OPTION_S);
        }
        parameter.setSrcPath(new File(cmd.getOptionValue(OPTION_S)).getCanonicalPath());
        if (!FileUtil.isDir(parameter.getSrcPath())) {
            throw new IllegalArgumentException(
                            "Source path doesn't exist or isn't a directory, path: "
                                            + parameter.getSrcPath());
        }

        // Destination path
        if (!cmd.hasOption(OPTION_D)) {
            throw new IllegalArgumentException(MISSING_OPTION_MSG + OPTION_D);
        }
        parameter.setDestPath(new File(cmd.getOptionValue(OPTION_D)).getCanonicalPath());
        if (!FileUtil.isDir(parameter.getDestPath())) {
            throw new IllegalArgumentException(
                            "Destination path doesn't exist or isn't a directory, path: "
                                            + parameter.getDestPath());
        }

        // Source path and destination path cannot be sub path
        if (FileUtil.isSubFile(new File(parameter.getSrcPath()),
                        new File(parameter.getDestPath()))) {
            throw new IllegalArgumentException(
                            "Source path is the sub path of dest path, or dest path is the sub path of source path.");
        }

        // Threads
        if (cmd.hasOption(OPTION_T)) {
            String thread = cmd.getOptionValue(OPTION_T);
            try {
                int threadNum = Integer.parseInt(thread);
                if (threadNum <= 0 || threadNum > RESTORE_THREAD_MAX) {
                    throw new IllegalArgumentException("Invalid thread number: " + thread
                                    + ", should be in [1," + RESTORE_THREAD_MAX + "]");
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
        return parameter;
    }

    private static byte[] getRestoreKey(Console console) throws Exception {
        StatisticsHelper.prt("Enter restore key: ");
        char[] passwordArray = console.readPassword();
        return new String(passwordArray).getBytes(Constants.UTF_8);
    }

    /**
     * Resolve restore parameter.
     * 
     * @param args Arguments
     * @param console Console
     * @return RestoreParameter
     * @throws Exception Exception
     */
    public static RestoreParameter resolveRestoreParameter(String[] args, Console console)
                    throws Exception {
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

        // Get restore key
        parameter.setKeyBytes(getRestoreKey(console));

        return parameter;
    }
}
