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
public class FileMetadataHelper {

    /**
     * Get metadata JSON string from FileMetaData object.
     * 
     * @param metaData FileMetaData
     * @return JSON string
     * @throws Exception Exception
     */
    public static String getFileMetadataJSON(FileMetadata metaData) throws Exception {
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
    public static FileMetadata getFileMetadataObj(byte[] bytes) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileMetadata metaData = mapper.readValue(bytes, FileMetadata.class);
        return metaData;
    }

    /**
     * Generate file metadata.
     * 
     * @param srcBasePath src base path
     * @param srcFullPath src full path
     * @return FileMetaData
     * @throws Exception Exception
     */
    public static FileMetadata generateFileMetadata(String srcBasePath, String srcFullPath)
                    throws Exception {
        FileMetadata metaData = new FileMetadata();
        File srcFile = new File(srcFullPath);
        metaData.setFileName(srcFile.getName());
        metaData.setFileBasePath(srcBasePath);
        metaData.setFileFullPath(srcFullPath);
        metaData.setFileLen(srcFile.length());
        metaData.setBackupTime(new Date().getTime());
        metaData.setBackupSWVersion(Constants.SW_VERSION);
        metaData.setAesVersion(Constants.AES_VERSION);
        metaData.setAesIV(HashUtil.generateRandomString(Constants.METADATA_IV_LEN));
        metaData.setKeySalt(HashUtil.generateRandomString(Constants.METADATA_KEY_SALT_LEN));
        // Calculating checksum has performance impact, skip here
        // metaData.setCheckSum(HashUtil.convertBytesToHexStr(HashUtil.getSHA256Hash(srcFile)));
        metaData.setObscure(HashUtil.generateRandomString(Constants.METADATA_OBSCURE_LEN_MIN,
                        Constants.METADATA_OBSCURE_LEN_MAX));
        return metaData;
    }
}
