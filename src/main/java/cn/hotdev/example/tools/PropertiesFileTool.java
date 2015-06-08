package cn.hotdev.example.tools;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;


public class PropertiesFileTool {
    private static final Logger log = LoggerFactory.getLogger(PropertiesFileTool.class);

    private Properties properties;
    private String fileName;
    private boolean isLoaded;

    public PropertiesFileTool(String fileName) {
        this.properties = new Properties();
        this.fileName = fileName;
        this.isLoaded = false;
        reload();
    }

    public void reload() {

        properties.clear();

        InputStream is = null;

        /* first: try to load the outside file */
        try {

            String cfgPath = "";
            String projectPath = getProjectPath(PropertiesFileTool.class);
            if (projectPath != null)
                cfgPath = projectPath + System.getProperty("file.separator", "/");
            cfgPath += fileName;

            File f = new File(cfgPath);
            if (f.exists()) {
                is = new FileInputStream(f);
                properties.load(is);
                isLoaded = true;
                log.info("Loaded outside properties: {}", fileName);
            }
        } catch (FileNotFoundException e) {
            log.info("Can't open outside properties: {}", fileName);
        } catch (IOException e) {
            log.info("Can't read outside properties: {}", fileName);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.info("Loaded open outside properties failed: {}, err={}", fileName, e.getMessage());
                }
            }
        }

        /* then: load the inside jar file */
        if (!isLoaded) {
            try {
                is = ClassLoader.getSystemResourceAsStream(fileName);

                if (is != null)
                    properties.load(is);

                isLoaded = true;
                log.info("Loaded inside properties: {}", fileName);
            } catch (FileNotFoundException e) {
                log.info("Can't open inside properties: {}", fileName);
            } catch (IOException e) {
                log.info("Can't read inside properties: {}", fileName);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.info("Loaded open inside properties failed: {}, err={}", fileName, e.getMessage());
                    }
                }
            }
        }
    }

    public String option(String name, String defaultValue) {
        if (name == null || name.isEmpty())
            return "";

        String property = properties.getProperty(name, defaultValue);
        if (property == null)
            return "";

        return property;
    }

    public int intOption(String name, String defaultValue) {
        if (name == null || name.isEmpty())
            return 0;

        String property = properties.getProperty(name, defaultValue);
        if (property == null)
            return 0;

        int v = 0;
        try {
            v = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            log.error("property '{}' to int failed: {}", name, property);
        }

        return v;
    }

    public long longOption(String name, String defaultValue) {
        if (name == null || name.isEmpty())
            return 0;

        String property = properties.getProperty(name, defaultValue);
        if (property == null)
            return 0;

        long v = 0;
        try {
            v = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            log.error("property '{}' to long failed: {}", name, property);
        }

        return v;
    }

    public double doubleOption(String name, String defaultValue) {
        if (name == null || name.isEmpty())
            return 0;

        String property = properties.getProperty(name, defaultValue);
        if (property == null)
            return 0;

        double v = 0;
        try {
            v = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            log.error("property '{}' to double failed: {}", name, property);
        }

        return v;
    }

    public boolean boolOption(String name, String defaultValue) {
        if (name == null || name.isEmpty())
            return false;

        String property = properties.getProperty(name, defaultValue);
        if (property == null)
            return false;

        return "true".equalsIgnoreCase(property);
    }

    public static String getProjectPath(Class cls) {
        URL uri = cls.getProtectionDomain().getCodeSource().getLocation();
        String path = uri.getPath();
        if (path.endsWith(".jar")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }
        File file = new File(path);
        return file.getAbsolutePath();
    }
}
