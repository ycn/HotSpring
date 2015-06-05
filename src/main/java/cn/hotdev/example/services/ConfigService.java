package cn.hotdev.example.services;

import org.springframework.stereotype.Service;

@Service
public interface ConfigService {

    public boolean getConfig(String key, boolean defaultValue);

    public int getConfig(String key, int defaultValue);

    public long getConfig(String key, long defaultValue);

    public double getConfig(String key, double defaultValue);

    public String getConfig(String key, String defaultValue);

    public <T> T getConfig(String key, Class<T> type);
}
