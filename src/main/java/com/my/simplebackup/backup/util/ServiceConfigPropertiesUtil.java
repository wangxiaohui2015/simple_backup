package com.my.simplebackup.backup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.my.simplebackup.backup.BackupMain;

/**
 * Service configuration properties utility.
 * 
 * @author Administrator
 */
public class ServiceConfigPropertiesUtil {

    private static Logger logger = Logger.getLogger(ServiceConfigPropertiesUtil.class);
    private static Properties properties = new Properties();
    private static final ServiceConfigPropertiesUtil instance = new ServiceConfigPropertiesUtil();

    private String configFilePath = "";

    private static String KEY_BACKUP_KEY = "backup.key";
    private static String KEY_BACKUP_THREADS = "backup.threads";
    private static String DEFAULT_SECURITY_KEY = "changeme";

    private ServiceConfigPropertiesUtil() {
        configFilePath = BackupMain.getRootDir() + File.separator + "conf" + File.separator
                        + "service_config.properties";
        try {
            InputStream in = new FileInputStream(configFilePath);
            properties.load(in);
        } catch (IOException e) {
            logger.error("Failed to init ServiceConfigPropertiesUtil, service exits.", e);
            System.exit(-1);
        }
    }

    /**
     * Get singleton object.
     * 
     * @return singleton object.
     */
    public static ServiceConfigPropertiesUtil getInstance() {
        return instance;
    }

    /**
     * Get backup security key, the default key is "changeme".
     * 
     * @return backup security key.
     */
    public String getBackupKey() {
        String key = properties.getProperty(KEY_BACKUP_KEY);
        if (null == key || "".equals(key)) {
            key = DEFAULT_SECURITY_KEY;
        }
        return key;
    }

    /**
     * Get backup threads number, the default value is 3.
     * 
     * @return backup threads number.
     */
    public int getThreadNumber() {
        String strVal = properties.getProperty(KEY_BACKUP_THREADS);
        int threadNumber = 3;
        try {
            int temThreadNumber = Integer.parseInt(strVal);
            if (temThreadNumber < 1 || temThreadNumber > 20) {
                logger.warn("Thread number is invalid, " + temThreadNumber + ", use default: "
                                + threadNumber);
            } else {
                threadNumber = temThreadNumber;
            }
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException occurred when get thread number.", e);
        }
        return threadNumber;
    }

    /**
     * Get value by key.
     * 
     * @param key key
     * @return value.
     */
    public String getProperties(String key) {
        return properties.getProperty(key);
    }
}
