package cn.hotdev.example.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface CacheService {

    public String get(String key);

    public <T> T get(String key, Class<T> type);

    public Map<String, String> getAll(Iterable<? extends String> keys);

    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type);

    public void set(String key, Object obj);

    public void invalid(String key);

}
