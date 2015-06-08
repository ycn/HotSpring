package cn.hotdev.example.services;


import cn.hotdev.example.tools.ObjectTool;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@NoArgsConstructor
@Service
public class ConfigServiceImpl implements ConfigService {

    private static final String PREFIX = "config:";

    @Autowired
    @Qualifier("PersistCacheService")
    private CacheService cacheService;

    @Override
    public boolean getConfig(String key, boolean defaultValue) {
        String value = getFromCache(key);
        if (value == null)
            return defaultValue;
        return "true".equals(value);
    }

    @Override
    public int getConfig(String key, int defaultValue) {
        String value = getFromCache(key);
        if (value == null)
            return defaultValue;
        int result = 0;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException ignore) {
        }
        return result;
    }

    @Override
    public long getConfig(String key, long defaultValue) {
        String value = getFromCache(key);
        if (value == null)
            return defaultValue;
        long result = 0;
        try {
            result = Long.parseLong(value);
        } catch (NumberFormatException ignore) {
        }
        return result;
    }

    @Override
    public double getConfig(String key, double defaultValue) {
        String value = getFromCache(key);
        if (value == null)
            return defaultValue;
        double result = 0;
        try {
            result = Double.parseDouble(value);
        } catch (NumberFormatException ignore) {
        }
        return result;
    }

    @Override
    public String getConfig(String key) {
        return getConfig(key, "");
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        String value = getFromCache(key);
        if (value == null)
            return defaultValue;
        return value;
    }

    @Override
    public <T> T getConfig(String key, Class<T> type) {
        String value = getFromCache(key);
        if (value != null) {
            try {
                return ObjectTool.unserialize(value, type);
            } catch (IOException ignore) {
            }
        }
        return null;
    }

    private String getFromCache(String key) {
        return cacheService.get(PREFIX + key);
    }
}
