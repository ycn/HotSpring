package cn.hotdev.example.services;

import cn.hotdev.example.models.cache.PersistObjectCache;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@NoArgsConstructor
@Service
@Qualifier("PersistCacheService")
public class PersistCacheServiceImpl implements CacheService {

    private PersistObjectCache cache = PersistObjectCache.getInstance();

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return cache.get(key, type);
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
        // do nothing
    }

    @Override
    public void invalid(String key) {
        cache.invalidate(key);
    }

}
