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
     * @param metadata FileMetadata
     * @return JSON string
     * @throws Exception Exception
     */
    public static String getFileMetadataJSON(FileMetadata metadata) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(metadata);
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
        FileMetadata metadata = mapper.readValue(bytes, FileMetadata.class);
        return metadata;
    }

    /**
     * 
     * Generate file metadata.
     * 
     * @param srcBasePath src base path
     * @param srcFullPath src full path
     * @param destFullPath dest full path
     * @param backupTime backup time
     * @param enableChecksum Enable checksum
     * @return FileMetadata
     * @throws Exception Exception
     */
    public static FileMetadata generateFileMetadata(String srcBasePath, String srcFullPath,
                    String destFullPath, long backupTime, boolean enableChecksum) throws Exception {
        FileMetadata metadata = new FileMetadata();
        File srcFile = new File(srcFullPath);
        metadata.setFileName(srcFile.getName());
        metadata.setFileBasePath(srcBasePath);
        metadata.setFileFullPath(srcFullPath);
        metadata.setFileLen(srcFile.length());
        metadata.setDestFileFullPath(destFullPath);
        metadata.setBackupTime(backupTime);
        metadata.setBackupSWVersion(Constants.SW_VERSION);
        metadata.setAesVersion(Constants.AES_VERSION);
        metadata.setAesIV(HashUtil.generateRandomString(Constants.METADATA_IV_LEN));
        metadata.setKeySalt(HashUtil.generateRandomString(Constants.METADATA_KEY_SALT_LEN));
        if (enableChecksum) {
            metadata.setChecksum(HashUtil.convertBytesToHexStr(HashUtil.getSHA256Hash(srcFile)));
        }
        metadata.setObscure(HashUtil.generateRandomString(Constants.METADATA_OBSCURE_LEN_MIN,
                        Constants.METADATA_OBSCURE_LEN_MAX));
        return metadata;
    }

    /**
     * Save metadata list to file
     * 
     * @param list metadata list
     * @param destFile destination file
     * @throws Exception Exception
     */
    public static void saveMetadataListToFile(List<FileMetadata> list, String destFile)
                    throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.writeValue(new File(destFile), list);
    }
}
