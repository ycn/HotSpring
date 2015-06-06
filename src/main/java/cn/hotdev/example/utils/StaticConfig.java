package cn.hotdev.example.utils;


import cn.hotdev.example.constants.ConfigOption;

import java.util.concurrent.atomic.AtomicReference;

public class StaticConfig {

    private static final AtomicReference<StaticConfig> instance = new AtomicReference<StaticConfig>();

    private PropertiesFileTool delegate;
    private PropertiesFileTool dockerDelegate;

    public static StaticConfig getInstance() {
        if (instance.get() == null) {
            instance.compareAndSet(null, new StaticConfig());
        }
        return instance.get();
    }

    private StaticConfig() {
        delegate = new PropertiesFileTool("config.properties");
        dockerDelegate = new PropertiesFileTool("docker.properties");
    }


    public boolean getBool(ConfigOption option) {
        return delegate.boolOption(option);
    }

    public int getInt(ConfigOption option) {
        return delegate.intOption(option);
    }

    public long getLong(ConfigOption option) {
        return delegate.longOption(option);
    }

    public double getDouble(ConfigOption option) {
        return delegate.doubleOption(option);
    }

    public String get(ConfigOption option) {
        if (ConfigOption.docker_host.equals(option)) {
            return dockerDelegate.option(option);
        } else {
            return delegate.option(option);
        }
    }

}
