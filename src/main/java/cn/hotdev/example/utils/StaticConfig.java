package cn.hotdev.example.utils;


import cn.hotdev.example.constants.ConfigOption;

import java.util.concurrent.atomic.AtomicReference;

public class StaticConfig {

    private static final AtomicReference<StaticConfig> instance = new AtomicReference<StaticConfig>();

    private PropertiesFileTool delegate;

    public static StaticConfig getInstance() {
        if (instance.get() == null) {
            instance.compareAndSet(null, new StaticConfig());
        }
        return instance.get();
    }

    private StaticConfig() {
        delegate = new PropertiesFileTool("config.properties");
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
        return delegate.option(option);
    }

}
