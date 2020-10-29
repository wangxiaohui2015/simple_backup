package com.my.simplebackup.restore;

import java.io.Console;
import java.io.File;

import com.my.simplebackup.common.FileUtil;
import com.my.simplebackup.common.ProgressUtil;
import com.my.simplebackup.common.TimeUtil;

/**
 * The main entry to decrypt file.
 * 
 * @author Administrator
 *
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
                prt("Invalid input, enter again: ");
            }
        }
    }

    public static void prt(String str) {
        System.out.print(str);
    }

    public static void prt(int str) {
        System.out.print(str);
    }

    public static void prtln(String str) {
        System.out.println(str);
    }

    public static void prtln(int str) {
        System.out.println(str);
    }

    private static void executeRestoreTask(File sourceFile, File destFile, String key) {
        File[] files = sourceFile.listFiles();
        for (File file : files) {
            File newDestFile =
                            new File(destFile.getAbsoluteFile() + File.separator + file.getName());
            if (file.isFile()) {
                try {
                    FileDecryptor.decryptFile(key, file.getAbsolutePath(),
                                    newDestFile.getAbsolutePath());
                    progressUtil.showProgressByIncress(file.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                newDestFile.mkdir();
                executeRestoreTask(file, newDestFile, key);
            }
        }
    }

    private static void startToRestore() {
        prtln("");
        prtln("*********** WELCOME TO DATA RESTORE *************");
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

        // Append source file name to dest file path
        destFile = new File(destFile.getAbsolutePath() + File.separator + sourceFile.getName());

        long totalSize = FileUtil.getDirSize(sourceFile);
        progressUtil = new ProgressUtil(totalSize);
        prtln("");
        prtln("Data Size: " + FileUtil.getFileSizeString(totalSize));
        prtln("");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        prtln("Executing...");
        prtln("");
        long startTime = System.currentTimeMillis();
        executeRestoreTask(sourceFile, destFile, password);
        long endTime = System.currentTimeMillis();
        String timeTakenStr = TimeUtil.calculateElapsedTime(startTime, endTime);
        prtln("");
        prtln("");
        prtln("Time taken: " + timeTakenStr);
        prtln("");
    }

    public static void main(String[] args) {
        startToRestore();
    }
}
