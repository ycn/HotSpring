package cn.hotdev.example.tools;


import cn.hotdev.example.models.enums.DefaultValueEnum;

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


    public boolean getBool(DefaultValueEnum option) {
        return delegate.boolOption(option.getName(), option.getDefaultValue());
    }

    public int getInt(DefaultValueEnum option) {
        return delegate.intOption(option.getName(), option.getDefaultValue());
    }

    public long getLong(DefaultValueEnum option) {
        return delegate.longOption(option.getName(), option.getDefaultValue());
    }

    public double getDouble(DefaultValueEnum option) {
        return delegate.doubleOption(option.getName(), option.getDefaultValue());
    }

    public String get(DefaultValueEnum option) {
        return delegate.option(option.getName(), option.getDefaultValue());
    }

}
