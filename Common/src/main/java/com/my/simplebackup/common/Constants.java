package com.my.simplebackup.common;

/**
 * Constants.
 */
public class Constants {
    public static final String UTF_8 = "UTF-8";
    public static final String SW_VERSION = "SimpleBackup-3.0.0";
    public static final String AES_VERSION = "AES256 with cipher AES/CBC/PKCS5PADDING";

    public static final int METADATA_OBSCURE_LEN_MIN = 1;
    public static final int METADATA_OBSCURE_LEN_MAX = 32;
    public static final int METADATA_IV_LEN = 32;
    public static final int METADATA_KEY_SALT_LEN = 32;

    public static final int BACKUP_TARGET_DIR_LEN = 6;
    public static final String BACKUP_TARGET_FILE_POST_FIX = ".data";

    public static final String BACKUP_MAIN_ROOT_DIR_KEY = "rootDir";
}
