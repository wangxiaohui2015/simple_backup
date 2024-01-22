package com.my.simplebackup.backup.record;

import java.util.ArrayList;
import java.util.List;

/**
 * RecordConfig class.
 */
public class RecordConfig {
    private List<RecordItem> records = new ArrayList<RecordItem>();

    public RecordConfig() {
    }

    public RecordConfig(List<RecordItem> records) {
        this.records = records;
    }

    public List<RecordItem> getRecords() {
        return records;
    }

    public void setRecords(List<RecordItem> records) {
        this.records = records;
    }
}
