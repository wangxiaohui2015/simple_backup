package com.my.simplebackup.restore;

import java.io.Console;
import java.io.File;
import java.io.UnsupportedEncodingException;

import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.ProgressUtil;
import com.my.simplebackup.common.TimeUtil;

/**
 * The main entry to decrypt file.
 */
public class RestoreMain {

    private static ProgressUtil progressUtil = null;

    private static Console console = null;

    static {
        console = System.console();
        if (console == null) {
            System.out.println("Cannot get console instance.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            startToRestore();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void startToRestore() throws UnsupportedEncodingException {
        prtln("");
        prtln("*********** WELCOME TO DATA RESTORE *************");
        prtln("*********** Version: " + Constants.SW_VERSION + " *********");
        prtln("");
        File sourceFile = null;
        File destFile = null;

        while (true) {
            String sourceDir = enterString("Enter source dir: ", 1, 256);
            sourceFile = new File(sourceDir);
            if (!sourceFile.exists() || !sourceFile.isDirectory()) {
                prtln("Invalid input, source dir doesn't exist or isn't a directory.");
                continue;
            }

            while (true) {
                String destDir = enterString("Enter target dir: ", 1, 256);
                destFile = new File(destDir);
                if (!destFile.exists() || !destFile.isDirectory()) {
                    prtln("Invalid input, dest dir doesn't exist or isn't a directory.");
                    continue;
                }
                break;
            }

            if (FileUtil.isSubFile(sourceFile, destFile)) {
                prtln("Source dir cannot be the sub dir of dest dir, and dest dir cannot be the sub dir of source dir.");
                continue;
            }
            break;
        }

        String password = enterPassword("Enter password: ", 1, 256);
        byte[] keyBytes = password.getBytes(Constants.UTF_8);
        password = "";

        prtln("");
        prtln("Calculating data size...");
        long totalSize = FileUtil.getDirSize(sourceFile);
        progressUtil = new ProgressUtil(totalSize);
        prtln("Data Size: " + FileUtil.getFileSizeString(totalSize));
        prtln("");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        prtln("Executing restore...");
        prtln("");
        long startTime = System.currentTimeMillis();
        executeRestoreTask(sourceFile, destFile, keyBytes);
        long endTime = System.currentTimeMillis();
        String timeTakenStr = TimeUtil.calculateElapsedTime(startTime, endTime);
        prtln("");
        prtln("Finished, time taken: " + timeTakenStr);
        prtln("");
    }

    private static String enterString(String message, int minLen, int maxLen) {
        prt(message);
        while (true) {
            String str = console.readLine();
            if (str.length() >= minLen && str.length() <= maxLen) {
                return str;
            } else {
                prt("Invalid input, enter again: ");
            }
        }
    }

    private static String enterPassword(String message, int minLen, int maxLen) {
        prt(message);
        while (true) {
            char[] passwordArray = console.readPassword();
            if (null != passwordArray) {
                String str = new String(passwordArray);
                if (str.length() >= minLen && str.length() <= maxLen) {
                    return str;
                }
            } else {
                prtln("Invalid input, enter again: ");
            }
        }
    }

    private static void prt(String str) {
        System.out.print(str);
    }

    private static void prtln(String str) {
        System.out.println(str);
    }

    private static void executeRestoreTask(File sourceDir, File destBaseDir, byte[] keyBytes) {
        File[] files = sourceDir.listFiles();
        if (null == files) {
            prtln("files is null when executing restore task.");
        }
        for (File file : files) {
            if (file.isFile()) {
                try {
                    RestoreService.restoreFile(file, destBaseDir, keyBytes);
                    double percentage = progressUtil.getProgress(file.length());
                    String percentageStr = String.format("%.2f", percentage);
                    prt("Completed " + percentageStr + "%. \r");
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            } else {
                executeRestoreTask(file, destBaseDir, keyBytes);
            }
        }
    }
}
