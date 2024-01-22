package com.my.simplebackup.common.metadata;

import java.io.File;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.simplebackup.common.Constants;
import com.my.simplebackup.common.HashUtil;

/**
 * File metadata helper.
 */
public class FileMetaDataHelper {

    /**
     * Get metadata JSON string from FileMetaData object.
     * 
     * @param metaData FileMetaData
     * @return JSON string
     * @throws Exception Exception
     */
    public static String getFileMetaDataJSON(FileMetaData metaData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(metaData);
            return json;
        } catch (JsonProcessingException e) {
            throw e;
        }
    }

    /**
     * Get FileMetaData object from JSON string bytes.
     * 
     * @param bytes JSON string bytes.
     * @return FileMetaData
     * @throws Exception Exception
     */
    public static FileMetaData getFileMetaDataObj(byte[] bytes) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileMetaData metaData = mapper.readValue(bytes, FileMetaData.class);
        return metaData;
    }

    /**
     * Generate file metadata.
     * 
     * @param srcFullPath src full path
     * @param srcBasePath src base path
     * @return FileMetaData
     * @throws Exception Exception
     */
    public static FileMetaData generateFileMetaData(String srcFullPath, String srcBasePath) throws Exception {
        FileMetaData metaData = new FileMetaData();
        File srcFile = new File(srcFullPath);
        metaData.setFileName(srcFile.getName());
        metaData.setFileBasePath(srcBasePath);
        metaData.setFileFullPath(srcFullPath);
        metaData.setFileLen(srcFile.length());
        metaData.setBackupTime(new Date().getTime());
        metaData.setBackupSWVersion(Constants.SW_VERSION);
        metaData.setAesVersion(Constants.AES_VERSION);
        metaData.setAesIV(HashUtil.generateRandomString(32));
        metaData.setKeySalt(HashUtil.generateRandomString(32));
        metaData.setCheckSum(HashUtil.convertBytesToHexStr(HashUtil.getSHA256Hash(srcFile)));
        metaData.setObscure(HashUtil.generateRandomString(1, 128));
        return metaData;
    }
}
