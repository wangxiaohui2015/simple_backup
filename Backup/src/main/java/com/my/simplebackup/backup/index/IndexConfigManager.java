package com.my.simplebackup.backup.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.my.simplebackup.common.HashUtil;

/**
 * Index configure manager.
 * 
 */
public class IndexConfigManager {

    private String configFilePath;

    public static final String DEFAULT_CONFIG_FILE_NAME = "index.json";

    private static final long MAX_INDEX = 999999999999999L;

    public IndexConfigManager(String configRootPath) {
        this.configFilePath = configRootPath + File.separator + "conf" + File.separator + DEFAULT_CONFIG_FILE_NAME;
    }

    /**
     * Get index configure object from file index.json
     * 
     * @return IndexConfig
     * @throws Exception Exception
     */
    public IndexConfig getIndexConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if (!new File(this.configFilePath).exists()) {
            throw new Exception("Missing index config file when getting index config, " + this.configFilePath);
        }
        IndexConfig config = mapper.readValue(new File(this.configFilePath), IndexConfig.class);
        return config;
    }

    /**
     * Save index configure to file index.json
     * 
     * @param config IndexConfig
     * @throws Exception Exception
     */
    public void saveIndexConfig(IndexConfig config) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.writeValue(new File(this.configFilePath), config);
    }

    /**
     * Generate index file list.
     * 
     * @param num How many index file need to be generated.
     * @return List<IndexFile>
     * @throws Exception Exception
     */
    public synchronized List<IndexFile> generateIndexFileList(int num) throws Exception {
        List<IndexFile> results = new ArrayList<IndexFile>();
        if (num <= 0) {
            return results;
        }
        IndexConfig config = getIndexConfig();
        long currentIndex = config.getIndex();
        if (currentIndex >= MAX_INDEX) {
            throw new Exception("Reached the max index number, failed to generate new index.");
        }
        for (int i = 0; i < num; i++) {
            currentIndex += 1;
            long currentFolderIndex = currentIndex / 1000;
            String folderPath = convertNumToFolderPath(currentFolderIndex);
            String fileName = generateRandomFileName();
            String filePath = folderPath + fileName;
            IndexFile indexfile = new IndexFile(fileName, filePath);
            results.add(indexfile);
        }
        config.setIndex(currentIndex);
        saveIndexConfig(config);
        return results;
    }

    /**
     * Convert number to folder path.
     * 
     * @param num Index number
     * @return Folder path
     */
    private String convertNumToFolderPath(long num) {
        String result = "";
        long curNum = num;
        while (curNum != 0) {
            long mod = curNum % 1000;
            String subFolder = String.format("%03d", mod) + File.separator;
            result = subFolder + result;
            curNum = curNum / 1000;
        }
        return result;
    }

    /**
     * Generate random file name.
     * 
     * @return Random file name.
     * @throws Exception Exception
     */
    private String generateRandomFileName() throws Exception {
        String ramdomStr = HashUtil.generateRandomString(32);
        long currentTime = new Date().getTime();
        byte[] bytes = HashUtil.getSHA256Hash((ramdomStr + currentTime).getBytes());
        String result = HashUtil.convertBytesToHexStr(bytes) + ".data";
        return result;
    }
}
