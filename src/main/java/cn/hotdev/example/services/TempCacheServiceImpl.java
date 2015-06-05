package cn.hotdev.example.services;


import cn.hotdev.example.models.cache.TempObjectCache;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
@Service
@Qualifier("TempCacheService")
public class TempCacheServiceImpl implements CacheService {

    private TempObjectCache cache = TempObjectCache.getInstance();

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        try {
            return cache.get(key, type);
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public Map<String, String> getAll(Iterable<? extends String> keys) {
        return cache.getAll(keys);
    }

    @Override
    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type) {
        return cache.getAll(keys, type);
    }

    @Override
    public void set(String key, Object obj) {
        cache.set(key, obj);
    }

    @Override
    public void invalid(String key) {
        cache.invalidate(key);
    }
}
