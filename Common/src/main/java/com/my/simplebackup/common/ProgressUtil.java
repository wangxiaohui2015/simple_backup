package com.my.simplebackup.common;

/**
 * Progress utility, used to show progress when encrypt or decrypt.
 */
public class ProgressUtil {
    private long totalPoints = 0;
    private long incressedPoints = 0;

    /**
     * Constructor method.
     * 
     * @param totalPoints total points, in general, we need to pass the size of a
     *                    file or a directory.
     */
    public ProgressUtil(long totalPoints) {
        super();
        this.totalPoints = totalPoints;
    }

    /**
     * Get progress.
     * 
     * @param incressedPoint Incress point
     */
    public int getProgress(long incressedPoint) {
        incressedPoints += incressedPoint;
        int percentage = (int) ((incressedPoints * 1.0 / totalPoints) * 100);
        return percentage;
    }
}
