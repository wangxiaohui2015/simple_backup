package com.my.simplebackup.common.metadata;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
     * 
     * Generate file metadata.
     * 
     * @param srcBasePath    src base path
     * @param srcFullPath    src full path
     * @param destFullPath   dest full path
     * @param backupTime     backup time
     * @param enableChecksum Enable checksum
     * @return
     * @throws Exception
     */
    public static FileMetadata generateFileMetadata(String srcBasePath, String srcFullPath, String destFullPath,
            long backupTime, boolean enableChecksum) throws Exception {
        FileMetadata metaData = new FileMetadata();
        File srcFile = new File(srcFullPath);
        metaData.setFileName(srcFile.getName());
        metaData.setFileBasePath(srcBasePath);
        metaData.setFileFullPath(srcFullPath);
        metaData.setFileLen(srcFile.length());
        metaData.setDestFileFullPath(destFullPath);
        metaData.setBackupTime(backupTime);
        metaData.setBackupSWVersion(Constants.SW_VERSION);
        metaData.setAesVersion(Constants.AES_VERSION);
        metaData.setAesIV(HashUtil.generateRandomString(Constants.METADATA_IV_LEN));
        metaData.setKeySalt(HashUtil.generateRandomString(Constants.METADATA_KEY_SALT_LEN));
        if (enableChecksum) {
            metaData.setChecksum(HashUtil.convertBytesToHexStr(HashUtil.getSHA256Hash(srcFile)));
        }
        metaData.setObscure(
                HashUtil.generateRandomString(Constants.METADATA_OBSCURE_LEN_MIN, Constants.METADATA_OBSCURE_LEN_MAX));
        return metaData;
    }

    /**
     * Save metadata list to file
     * 
     * @param list     metadata list
     * @param destFile destination file
     * @throws Exception Exception
     */
    public static void saveMetadataListToFile(List<FileMetadata> list, String destFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.writeValue(new File(destFile), list);
    }
}
