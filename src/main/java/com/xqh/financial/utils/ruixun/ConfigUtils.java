package com.xqh.financial.utils.ruixun;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

    private static String     propPath = File.separator + "config.properties";
    private static Properties prop     = null;

    static {
        prop = new Properties();
        InputStream in = ClassLoader.getSystemResourceAsStream(propPath);
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String keyName) {
        return prop.getProperty(keyName);
    }

    public static String getProperty(String keyName, String defaultValue) {
        return prop.getProperty(keyName, defaultValue);
    }

}
