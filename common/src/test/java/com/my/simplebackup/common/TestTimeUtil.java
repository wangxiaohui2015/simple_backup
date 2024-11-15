package com.my.simplebackup.common;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class TestTimeUtil {

    @Test
    public void testClculateElapsedTime1() {
        long time1 = new Date().getTime();
        long time2 = time1;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("0 second", timeStr);
    }

    @Test
    public void testClculateElapsedTime2() {
        long time1 = new Date().getTime();
        long time2 = time1 - 10 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("10 seconds", timeStr);
    }

    @Test
    public void testClculateElapsedTime3() {
        long time1 = new Date().getTime();
        long time2 = time1 - 1 * 60 * 1000 - 10 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("1 minute, 10 seconds", timeStr);
    }

    @Test
    public void testClculateElapsedTime4() {
        long time1 = new Date().getTime();
        long time2 = time1 - 55 * 60 * 1000 - 1 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("55 minutes, 1 second", timeStr);
    }

    @Test
    public void testClculateElapsedTime5() {
        long time1 = new Date().getTime();
        long time2 = time1 - 1 * 60 * 60 * 1000 - 1 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("1 hour, 1 second", timeStr);
    }

    @Test
    public void testClculateElapsedTime6() {
        long time1 = new Date().getTime();
        long time2 = time1 - 5 * 60 * 60 * 1000 - 2 * 60 * 1000 - 2 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("5 hours, 2 minutes, 2 seconds", timeStr);
    }

    @Test
    public void testClculateElapsedTime7() {
        long time1 = new Date().getTime();
        long time2 = time1 - 5 * 60 * 60 * 1000 - 2 * 60 * 1000 - 0 * 1000;
        String timeStr = TimeUtil.calculateElapsedTime(time1, time2);
        assertEquals("5 hours, 2 minutes, 0 second", timeStr);
    }
}
