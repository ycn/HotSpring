package cn.hotdev.example.models.cache;


import cn.hotdev.example.constants.DefaultConfigOption;
import cn.hotdev.example.tools.ObjectTool;
import cn.hotdev.example.tools.RedisTool;
import cn.hotdev.example.tools.StaticConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PersistObjectCache implements ObjectCache {
    private static final AtomicReference<PersistObjectCache> instance = new AtomicReference<PersistObjectCache>();
    private static final Logger log = LoggerFactory.getLogger(PersistObjectCache.class);
    private static final StaticConfig config = StaticConfig.getInstance();
    private LoadingCache<String, String> cache;

    // redis 永久缓存DB
    private static final int redisDb = config.getInt(DefaultConfigOption.cache_reObj_redis_db);
    // redis client
    private RedisTool redisTool;

    public static PersistObjectCache getInstance() {
        if (instance.get() == null)
            instance.compareAndSet(null, new PersistObjectCache());
        return instance.get();
    }

    private PersistObjectCache() {

        redisTool = new RedisTool("PersistObjectCache");

        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(config.getInt(DefaultConfigOption.cache_reObj_concurrencyLevel))
                .maximumSize(config.getInt(DefaultConfigOption.cache_reObj_size))
                .expireAfterAccess(config.getInt(DefaultConfigOption.cache_reObj_expiration), TimeUnit.SECONDS)
                .refreshAfterWrite(config.getInt(DefaultConfigOption.cache_reObj_refresh), TimeUnit.SECONDS)
                .build(new PersistObjectCacheLoader(redisTool, redisDb));
    }

    @Override
    public Set<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public long count() {
        return cache.size();
    }

    @Override
    public int getDb() {
        return redisDb;
    }

    public String get(String key) {
        if (key == null || key.isEmpty())
            return "";

        try {
            return cache.getUnchecked(key);
        } catch (UncheckedExecutionException e) {
            log.warn("unchecked exception: key={}, db={}, err={}", key, redisDb, e.getMessage());
        }

        return "";
    }

    @Override
    public <T> T get(String key, Class<T> type) {

        String value = get(key);

        if (value == null || value.isEmpty()) {
            return null;
        }

        try {

            return ObjectTool.unserialize(value, type);

        } catch (IOException e) {
            log.warn("object unserialize exception: key={}, db={}, err={}", key, redisDb, e.getMessage());
        }

        return null;
    }

    public Map<String, String> getAll(Iterable<? extends String> keys) {

        Map<String, String> map = new HashMap<String, String>();
        if (keys == null) {
            return map;
        }

        try {

            ImmutableMap<String, String> allPresent = cache.getAll(keys);

            if (allPresent != null && !allPresent.isEmpty()) {
                for (String key : allPresent.keySet()) {

                    String value = allPresent.get(key);

                    if (value != null && !value.isEmpty()) {
                        map.put(key, value);
                    }
                }
            }

        } catch (ExecutionException | UncheckedExecutionException | ExecutionError e) {
            log.warn("unchecked exception: keys={}, db={}, err={}", keys, redisDb, e.getMessage());
        }

        return map;
    }

    @Override
    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type) {
        Map<String, String> allPresent = getAll(keys);
        Map<String, T> map = new HashMap<String, T>();

        if (allPresent == null || allPresent.isEmpty())
            return map;

        for (String key : allPresent.keySet()) {
            String value = allPresent.get(key);

            if (value != null && !value.isEmpty()) {

                try {
                    T t = ObjectTool.unserialize(value, type);
                    map.put(key, t);
                } catch (IOException e) {
                    log.warn("object unserialize exception: key={}, db={}, err={}", key, redisDb, e.getMessage());
                }

            }
        }

        return map;
    }


    public void invalidate(String key) {
        cache.invalidate(key);
    }

    public void cleanUp() {
        cache.cleanUp();
        redisTool.close();
    }
}
