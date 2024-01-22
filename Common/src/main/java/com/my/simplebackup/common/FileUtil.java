package com.my.simplebackup.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * File utility.
 */
public class FileUtil {

    /**
     * Close input stream.
     * 
     * @param in InputStream
     */
    public static void closeInputStream(InputStream in) {
        if (null != in) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close output stream.
     * 
     * @param out OutputStream
     */
    public static void closeOutputStream(OutputStream out) {
        if (null != out) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeRandomAccessFile(RandomAccessFile rFile) {
        if (null != rFile) {
            try {
                rFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Compare the given files, check if one of them is the sub file of another.
     * 
     * @param file1 File
     * @param file2 File
     * @return true: sub file: false: not sub file
     */
    public static boolean isSubFile(File file1, File file2) {
        if (null == file1 || null == file2) {
            return false;
        }
        if (file1.getAbsolutePath().indexOf(file2.getAbsolutePath()) != -1) {
            return true;
        }
        if (file2.getAbsolutePath().indexOf(file1.getAbsolutePath()) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Get file size, byte
     * 
     * @param file file or directory
     * @return file or directory size, byte
     */
    public static long getDirSize(File file) {
        if (null != file && file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children) {
                    size += getDirSize(f);
                }
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    /**
     * Get file size string by file bytes
     * 
     * @param fileSize file size, bytes
     * @return String format file size
     */
    public static String getFileSizeString(long fileSize) {

        long tb = fileSize / (1024L * 1024 * 1024 * 1024);
        long gb = (fileSize - tb * (1024L * 1024 * 1024 * 1024)) / (1024L * 1024 * 1024);
        long mb = (fileSize - tb * (1024L * 1024 * 1024 * 1024) - gb * 1024L * 1024 * 1024) / (1024L * 1024);
        long kb = (fileSize - tb * (1024L * 1024 * 1024 * 1024) - gb * 1024L * 1024 * 1024 - mb * 1024L * 1024)
                / (1024);
        long b = (fileSize - tb * (1024L * 1024 * 1024 * 1024) - gb * 1024L * 1024 * 1024 - mb * 1024L * 1024
                - kb * 1024);
        StringBuffer sb = new StringBuffer();

        if (tb != 0) {
            sb.append(tb + "TB, ");
        }
        if (gb != 0) {
            sb.append(gb + "GB, ");
        }
        if (mb != 0) {
            sb.append(mb + "MB, ");
        }
        if (kb != 0) {
            sb.append(kb + "KB, ");
        }
        if (b != 0) {
            sb.append(b + "B");
        }

        return sb.toString();
    }
}
