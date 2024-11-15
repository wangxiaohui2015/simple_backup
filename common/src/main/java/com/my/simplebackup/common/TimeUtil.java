
package com.my.simplebackup.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Time utility.
 */
public class TimeUtil {

    private static SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final long HOUR = 60 * 60 * 1000L;
    private static final long MINUTE = 60 * 1000L;
    private static final long SECOND = 1000L;

    /**
     * Calculate elapsed time.
     * 
     * @param time1 Time1
     * @param time2 Time2
     * @return Elapsed time.
     */
    public static String calculateElapsedTime(long time1, long time2) {
        long timeDiff = Math.abs(time1 - time2);
        long hours = timeDiff / HOUR;
        long minutes = (timeDiff - hours * HOUR) / MINUTE;
        long seconds = (timeDiff - hours * HOUR - minutes * MINUTE) / SECOND;

        StringBuffer sb = new StringBuffer();
        if (hours > 0) {
            sb.append(hours + (hours > 1 ? " hours, " : " hour, "));
        }
        if (minutes > 0) {
            sb.append(minutes + (minutes > 1 ? " minutes, " : " minute, "));
        }
        sb.append(seconds + (seconds > 1 ? " seconds" : " second"));
        return sb.toString();
    }

    /**
     * Parse a string to Date object.
     * 
     * @param str string
     * @return Date object
     * @throws Exception Exception
     */
    public static Date parseStrToDate(String str) throws Exception {
        return format.parse(str);
    }

    public static String parseDateToStr(Date date) {
        return format.format(date);
    }
}
