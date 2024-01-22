package com.my.simplebackup.backup.index;

/**
 * Index configure.
 */
public class IndexConfig {
    private long index;

    public IndexConfig() {
    }

    public IndexConfig(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}
