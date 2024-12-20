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

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;
    private static final long TB = GB * 1024;

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

    /**
     * Close random access file.
     * 
     * @param rFile Random access file
     */
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
     * @throws IOException IOException
     */
    public static boolean isSubFile(File file1, File file2) throws IOException {
        if (null == file1 || null == file2) {
            return false;
        }
        if (file1.getCanonicalPath().indexOf(file2.getCanonicalPath()) != -1) {
            return true;
        }
        if (file2.getCanonicalPath().indexOf(file1.getCanonicalPath()) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Check if the given file path exist and is a file.
     * 
     * @param filePath file path
     * @return true: exist and it's a file
     */
    public static boolean isFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Check if the given file path exist and is a directory.
     * 
     * @param filePath file path
     * @return true: exist and it's a directory
     */
    public static boolean isDir(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isDirectory();
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
     * @param isAppendBytes is append bytes
     * @return String format file size
     */
    public static String getFileSizeString(long fileSize, boolean isAppendBytes) {
        StringBuilder sb = new StringBuilder();
        if (fileSize >= TB) {
            sb.append(String.format("%.2f TB", (double) fileSize / TB));
        } else if (fileSize >= GB) {
            sb.append(String.format("%.2f GB", (double) fileSize / GB));
        } else if (fileSize >= MB) {
            sb.append(String.format("%.2f MB", (double) fileSize / MB));
        } else if (fileSize >= KB) {
            sb.append(String.format("%.2f KB", (double) fileSize / KB));
        } else {
            sb.append(fileSize + " B");
        }
        if (isAppendBytes) {
            sb.append(" (" + fileSize + " bytes)");
        }
        return sb.toString();
    }

    /**
     * Get file size string by file bytes
     * 
     * @param fileSize file size, bytes
     * @return String format file size
     */
    public static String getFileSizeString(long fileSize) {
        return getFileSizeString(fileSize, true);
    }
}
