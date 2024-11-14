package com.my.simplebackup.common;

/**
 * String utility.
 */
public class StringUtil {

    /**
     * Check if a string is null or empty.
     * 
     * @param str String object
     * @return false if null or empty, true not null and empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
