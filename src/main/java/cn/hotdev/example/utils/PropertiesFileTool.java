package cn.hotdev.example.utils;


import cn.hotdev.example.constants.ConfigOption;

import java.io.*;
import java.net.URL;
import java.util.Properties;


public class PropertiesFileTool {
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
                System.out.println("Loaded outside properties " + fileName);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can't open outside properties " + fileName);
        } catch (IOException e) {
            System.out.println("Can't read outside properties " + fileName);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("Loaded open outside properties got exception " + e.getMessage());
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
                System.out.println("Loaded inside properties " + fileName);
            } catch (FileNotFoundException e) {
                System.out.println("Can't open inside properties " + fileName);
            } catch (IOException e) {
                System.out.println("Can't read inside properties " + fileName);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        System.out.println("Loaded open inside properties got exception " + e.getMessage());
                    }
                }
            }
        }
    }

    public String option(ConfigOption option) {
        if (option == null)
            return "";

        String property = properties.getProperty(option.name(), option.getDefaultValue());
        if (property == null)
            return "";

        return property;
    }

    public int intOption(ConfigOption option) {
        if (option == null)
            return 0;

        String property = properties.getProperty(option.name(), option.getDefaultValue());
        if (property == null)
            return 0;

        int v = 0;
        try {
            v = Integer.parseInt(property);
        } catch (NumberFormatException e) {
        }

        return v;
    }

    public long longOption(ConfigOption option) {
        if (option == null)
            return 0;

        String property = properties.getProperty(option.name(), option.getDefaultValue());
        if (property == null)
            return 0;

        long v = 0;
        try {
            v = Integer.parseInt(property);
        } catch (NumberFormatException e) {
        }

        return v;
    }

    public double doubleOption(ConfigOption option) {
        if (option == null)
            return 0;

        String property = properties.getProperty(option.name(), option.getDefaultValue());
        if (property == null)
            return 0;

        double v = 0;
        try {
            v = Double.parseDouble(property);
        } catch (NumberFormatException e) {
        }

        return v;
    }

    public boolean boolOption(ConfigOption option) {
        if (option == null)
            return false;

        String property = properties.getProperty(option.name(), option.getDefaultValue());
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
